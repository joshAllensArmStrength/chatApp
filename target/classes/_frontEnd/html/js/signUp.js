const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

signUpButton.addEventListener('click', () => {
	container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
	container.classList.remove("right-panel-active");
});

function heyMan() {
socket.emit("hey_man", "somerandomstring");
}