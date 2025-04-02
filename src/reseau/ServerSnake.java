package reseau;

import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import snake.controller.ControllerSnakeGame;
import snake.game.Snake;
import snake.game.SnakeGame;
import snake.model.InputMap;
import snake.utils.AgentAction;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerSnake {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 2;
    private static List<Socket> clients = new ArrayList<>();
    private static List<DataOutputStream> listClients = new ArrayList<>();

    private static SnakeGame snakeGame;
    private static String layoutPath;
    private static ObjectMapper mapper = new ObjectMapper();
    private static boolean debutJeu = false;

    public ServerSnake() {
        layoutPath = "src/snake/layouts/smallArenaNoWall.lay";
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

            try {
                mapper.writeValue(new File("game_state.json"), snakeGame);
                System.out.println("Fichier d'état crée");
            } catch (IOException e) {
                System.out.println("Problème d'init de la carte");
                e.printStackTrace();
            }

            while (clients.size() < MAX_CLIENTS) {
                try {
                    Socket so = ecoute.accept();
                    clients.add(so);
                    int currentNumClient = numClient;
                    new Thread(() -> {
                        try {
                            System.out.println("😊");
                            traiterClient(so, currentNumClient);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }).start();
                    numClient++;
                } catch (IOException e) {
                    System.out.println("Erreur lors de la communication avec un client");
                }
                System.out.println("Deux clients connectés");

                // if(clients.size() == MAX_CLIENTS){
                ControllerSnakeGame controllerSnakeGame = new ControllerSnakeGame(snakeGame, layoutPath);
                // }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la mise en place du serveur");
            e.printStackTrace();
        }
    }

    public static void traiterClient(Socket so, int numClient) throws InterruptedException {
        BufferedReader entree = null;
        DataOutputStream sortie = null;
        try {
            //while (!debutJeu) {
                System.out.println(numClient + ". Une nouvelle connexion a été ouverte");

                listClients.add(sortie);

                entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                sortie = new DataOutputStream(so.getOutputStream());

                int playerIndex = numClient - 1;
                System.out.println("Le client " + numClient + " contrôle le serpent " + playerIndex);
                sortie.writeUTF("NUMSNAKE :" + playerIndex);

                // Envoyer l'état initial du jeu
                // String gameStateJson = mapper.writeValueAsString(snakeGame); // Convertir
                // SnakeGame en JSON
                sortie.writeUTF("ETATINITIAL :" + mapper.writeValueAsString(snakeGame));

            //}
            System.out.println("En attente d'une donnée");
            String actionJson = entree.readLine(); // lit ce qui arrive

            System.out.println("afficher action : " + actionJson);
            System.out.println("Donnée reçue");
            while (!actionJson.equals("stop")) {
                if ((actionJson != null) && (actionJson != "updateReceived")) {
                    System.out.println("Action reçue du joueur " + numClient + ": " + actionJson);
                    // mettre update pour action dans carte
                    // snakeGame.takeTurn();
                    // snakeGame.keyPressed(null);
                    snakeGame.updateGame();
                    // Thread.sleep(1000);
                    sortie.writeUTF(mapper.writeValueAsString(snakeGame));
                    actionJson = entree.readLine();// lit la prochaine chaîne
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

/*
 * 
 * Ce qui fonctionne : 
 * - quand on la,nce le jeu, le serveur se met bien à jour à chaque tour
 * 
 * Ce qui ne fonctionne pas : 
 * - le serveur n'envoie pas ces nouveaux états aux joueurs
 * - le contrôle des snake se fait sur le serveur donc les deux snakes ont les mêmes flèches
 */