const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

signUpButton.addEventListener('click', () => {
	container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
	container.classList.remove("right-panel-active");
<<<<<<< HEAD
});
=======
});

function heyMan() {
socket.emit("hey_man", "somerandomstring");
}
>>>>>>> 5432e645d2dd8b7fe3e8ebbeb68951493f1f3fc7
