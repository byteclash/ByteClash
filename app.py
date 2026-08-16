from flask import Flask, render_template, request
from flask_socketio import SocketIO, emit

app = Flask(__name__)
socketio = SocketIO(app, cors_allowed_origins="*")

# Guardar jugadores en memoria usando su token único
players = {}  # { token: {"name": ..., "score": ..., "socket_id": ...} }

@app.route('/')
def index():
    return render_template('index.html')

@socketio.on('join_or_reconnect')
def handle_connect(data):
    token = data.get('token')
    name = data.get('name')

    if token not in players:
        # Nuevo jugador
        players[token] = {'name': name, 'score': 0, 'socket_id': request.sid}
    else:
        # Reconexión: actualiza el socket pero MANTIENE los puntos
        players[token]['socket_id'] = request.sid

    emit('player_state', players[token])

@socketio.on('add_points')
def handle_points(data):
    token = data.get('token')
    if token in players:
        players[token]['score'] += 100
        emit('player_state', players[token])

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=5000, debug=True)
