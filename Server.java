import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static class Player {
        public String name;
        public int score;

        public Player() {}
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static class JoinRequest {
        private String token;
        private String name;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class PointRequest {
        private String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    // Persistencia temporal en memoria basada en token
    private static final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8080);

        SocketIOServer server = new SocketIOServer(config);

        // Registro o reconexión de jugador
        server.addEventListener("join_or_reconnect", JoinRequest.class, (client, data, ackSender) -> {
            String token = data.getToken();
            String name = data.getName();

            players.putIfAbsent(token, new Player(name, 0));
            Player player = players.get(token);

            client.sendEvent("player_state", player);
        });

        // Sumar puntos
        server.addEventListener("add_points", PointRequest.class, (client, data, ackSender) -> {
            String token = data.getToken();
            Player player = players.get(token);
            if (player != null) {
                player.score += 100;
                client.sendEvent("player_state", player);
            }
        });

        server.start();
        System.out.println("Servidor Java Socket.io ejecutándose en http://localhost:8080");
    }
}
