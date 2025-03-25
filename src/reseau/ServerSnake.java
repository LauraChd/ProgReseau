package reseau;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import snake.game.SnakeGame;
import snake.model.InputMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerSnake {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 2;
    private static List<Socket> clients = new ArrayList<>();
    private static List<DataOutputStream> listClients = new ArrayList<>();

    private static SnakeGame snakeGame;
    private static ObjectMapper mapper = new ObjectMapper();

    public ServerSnake() {
        String layoutPath = "src/snake/layouts/smallArena.lay";
        try {
            ServerSnake.snakeGame = new SnakeGame(10000, new InputMap(layoutPath));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        int numClient = 1;
        try (ServerSocket ecoute = new ServerSocket(PORT)) {
            System.out.println("Serveur en attente de " + MAX_CLIENTS + " connexions");

            while (clients.size() < MAX_CLIENTS) {
                try {
                    Socket so = ecoute.accept();
                    clients.add(so);
                    int currentNumClient = numClient;
                    new Thread(() -> traiterClient(so, currentNumClient)).start();
                    numClient++;
                } catch (IOException e) {
                    System.out.println("Erreur lors de la communication avec un client");
                }
                System.out.println("Deux clients connectés");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la mise en place du serveur");
            e.printStackTrace();
        }
    }

    public static void traiterClient(Socket so, int numClient) {
        try {
            System.out.println(numClient + ". Une nouvelle connexion a été ouverte");

            BufferedReader entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
            DataOutputStream sortie = new DataOutputStream(so.getOutputStream());

            listClients.add(sortie);

            try{
                mapper.writeValue(new File("game_state.json"), snakeGame);
                System.out.println("Fichier d'état crée");
            } catch (IOException e){
                System.out.println("Problème d'init de la carte");
                e.printStackTrace();
            }

            // Envoyer l'état initial du jeu
            //String gameStateJson = mapper.writeValueAsString(snakeGame); // Convertir SnakeGame en JSON
            sortie.writeUTF(mapper.writeValueAsString(snakeGame));
           

            System.out.println("En attente d'une donnée");
            String actionJson = entree.readLine(); // on lit ce qui arrive
            System.out.println("Donnée reçue");
            while (!actionJson.equals("stop")) {
                if (actionJson != null) {
                    // Mise à jour du jeu (ici, il faudrait traiter la commande reçue)
                    System.out.println("Action reçue: " + actionJson);

                    // Mettre à jour le jeu (simulation de mise à jour)
                    snakeGame.updateGame();

                    // Envoyer l’état mis à jour du jeu
                    sortie.writeUTF(mapper.writeValueAsString(snakeGame));

                    actionJson = entree.readLine(); // Lire la prochaine chaîne
                }
            }
            System.out.println(numClient + ". Fin de communication demandée");
            sortie.writeUTF("stopOK");

        } catch (IOException e) {
            System.out.println(numClient + ". Problème\n" + e);
        } finally {
            try {
                so.close();
                System.out.println(numClient + ". La connexion a été fermée");
            } catch (IOException e) {
                System.out.println("Erreur de fermeture de socket : " + e.getMessage());
            }
        }
    }

    public static void sendUpdate(String str, DataOutputStream currentClient) {
        List<DataOutputStream> listeClients = listClients;
        for (int i = 0; i < listeClients.size(); i++) {
            DataOutputStream sortie = listeClients.get(i);
            if (sortie != currentClient) {
                try {
                    // Convertir SnakeGame mis à jour en JSON
                    String updatedGameStateJson = mapper.writeValueAsString(snakeGame);
                    sortie.writeUTF(updatedGameStateJson); // Envoyer l'état du jeu mis à jour
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            new ServerSnake().start();
        }

    }
}
