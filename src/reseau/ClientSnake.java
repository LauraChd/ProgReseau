package reseau;

import java.io.*;
import java.net.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import snake.game.SnakeGame;

public class ClientSnake {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private ObjectMapper mapper = new ObjectMapper();

    public void start() {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Recevoir l'état initial du jeu
            String gameStateJson = in.readLine();
            SnakeGame snakeGame = mapper.readValue(gameStateJson, SnakeGame.class);
            System.out.println("État initial du jeu reçu: " + gameStateJson);

            // Boucle pour gérer les actions du joueur
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("Entrez une action (UP/DOWN/LEFT/RIGHT): ");
                String action = userInput.readLine();
                if (action != null) {
                    out.println(action); // Envoi de l’action au serveur
                    String newStateJson = in.readLine();
                    snakeGame = mapper.readValue(newStateJson, SnakeGame.class);
                    System.out.println("État mis à jour: " + newStateJson);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientSnake().start();
    }
}
