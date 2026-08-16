const socket = io('http://localhost:8080');

// Generar token único guardado localmente en el móvil
let token = localStorage.getItem('user_token') || 'usr_' + Math.random().toString(36).substring(2, 11);
localStorage.setItem('user_token', token);

function entrar() {
  const name = document.getElementById('name').value;
  if (!name) return;
  localStorage.setItem('user_name', name);
  socket.emit('join_or_reconnect', { token, name });
}

function ganarPuntos() {
  socket.emit('add_points', { token });
}

// Reconexión automática tras caídas de internet celular
socket.on('connect', () => {
  const savedName = localStorage.getItem('user_name');
  if (savedName) {
    socket.emit('join_or_reconnect', { token, name: savedName });
  }
});

socket.on('player_state', (data) => {
  document.getElementById('setup').classList.add('hidden');
  document.getElementById('game').classList.remove('hidden');
  document.getElementById('user').innerText = 'Jugador: ' + data.name;
  document.getElementById('score').innerText = 'Puntos: ' + data.score;
});
