package backEnd

// Need to import the socket.io library
import com.corundumstudio.socketio._
import com.corundumstudio.socketio.listener._
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer


class Server() {

  var listConnectedClients: ListBuffer[SocketIOClient] = ListBuffer()
  var clientToUsername: Map[SocketIOClient, String] = Map()
  var usernameToClient: Map[String, SocketIOClient] = Map()
  var usernameToPassword: Map[String, String] = Map()
  var messagesSent: Map[String, ListBuffer[String]] = Map()

  // Set up the configuration for the server, will use localhost port 8080
  val config: Configuration = new Configuration {
    setHostname("localhost") // "localhost" means run on this machine
    setPort(8080)
  }

  // Initialize the socket server using the above configuration ^
  val server: SocketIOServer = new SocketIOServer(config)

  // Add listeners to the server that will execute code when certain events occur

  // client connects
  server.addConnectListener(new ConnectionListener(this))
  // client disconnects
  server.addDisconnectListener(new DisconnectionListener(this))
  // client sends a "register" message
  server.addEventListener("signUp", classOf[String], new SignUpUserListener(this))
  // client sends a "direct_message" message
  server.addEventListener("direct_message", classOf[String], new DMListener(this))

  server.start()

}

object Server {
  def main(args: Array[String]): Unit = {
    new Server()
  }
}

// Setup class for when a client connects to the server
class ConnectionListener(server: Server) extends ConnectListener {
  override def onConnect(client: SocketIOClient): Unit = {
    println("Connected: " + client) // Print a message saying the client has connected
    server.listConnectedClients += client // Add the client to the list of connected clients
    //println("Connection clients: " + server.listConnectedClients)
  }
}

// Setup class for when client disconnects from server
class DisconnectionListener(server: Server) extends DisconnectListener {
  override def onDisconnect(socket: SocketIOClient): Unit = {
    println("Disconnected: " + socket) // Print a message saying the client has disconnected
    server.listConnectedClients -= socket // Client disconnected, remove it form the list of connected clients
  }
}

// When someone registers
class SignUpUserListener(server: Server) extends DataListener[String] {
  override def onData(client: SocketIOClient, data: String, ackRequest: AckRequest): Unit = {
    // Here 'data' will be a json string in the format {"username":"someUserName","password":"somePassword"}
    val parsed: JsValue = Json.parse(data)
    val username: String = (parsed \ "username").as[String]
    val password: String = (parsed \ "password").as[String]
    if(server.usernameToPassword.contains(username)) {
      client.sendEvent("username_already_exists", "user already in database")
    }
    server.usernameToPassword += (username -> password)
    server.clientToUsername += (client -> username)
    server.usernameToClient += (username -> client)
    client.sendEvent("signed_up", username)
    //println("Server data: " + server.usernameToPassword) // for testing sign ups
    // client.sendEvent("chat_history", server.chatHistoryJSON())
  }
}

// Setup class for when client sends message to server
class DMListener(server: Server) extends DataListener[String] {
  override def onData(client: SocketIOClient, data: String, ackRequest: AckRequest): Unit = {
    //println("Received message: " + data + " from " + client) // Print information about the message received
    if(!server.messagesSent.contains(server.clientToUsername(client))) { // If client hasn't sent a message into the server, add client + message sent
      server.messagesSent += (server.clientToUsername(client) -> ListBuffer(data))
    }
    else if(server.messagesSent.contains(server.clientToUsername(client))) {
      server.messagesSent(server.clientToUsername(client)) += data
    }
    client.sendEvent("ACK", "I received your message of: " + data) // Let client know message was received to server
    // println("Messages sent: " + server.messagesSent)
  }
}