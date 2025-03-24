package reseau;

import java.io.*;
import java.net.*;
import snake.game.SnakeGame;
import snake.model.InputMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerSnake {
    private static final int PORT = 12345;
    private SnakeGame snakeGame;
    private ObjectMapper mapper = new ObjectMapper();

    public ServerSnake() {
        String layoutPath = "src/snake/layouts/alone.lay";
        try {
            this.snakeGame = new SnakeGame(10000, new InputMap(layoutPath));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur en attente de connexions...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connecté !");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Envoi de l’état initial du jeu
            out.println(mapper.writeValueAsString(snakeGame));

            while (true) {
                String actionJson = in.readLine();
                if (actionJson != null) {
                    // Mise à jour du jeu (ici, il faudrait traiter la commande reçue)
                    System.out.println("Action reçue: " + actionJson);
                    
                    // Mettre à jour le jeu (simulation de mise à jour)
                    snakeGame.updateGame(); 

                    // Envoyer l’état mis à jour du jeu
                    out.println(mapper.writeValueAsString(snakeGame));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerSnake().start();
    }
}
