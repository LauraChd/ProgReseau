package reseau;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import snake.game.Snake;
import snake.game.SnakeGame;
import snake.game.StrategieClavier;
import snake.model.InputMap;
import snake.utils.AgentAction;

public class ClientSnake {
    private static boolean end;
    private static final String SERVER_ADDRESS = "localhost"; //"192.168.47.129";
    private static final int PORT = 12345;
    private static ObjectMapper mapper = new ObjectMapper();
    private static SnakeGame snakeGame;
    private static int playerIndex;
    private static boolean debutJeu = false;
    private static String map;
    
        public void start() {
            end = false;
            Scanner scanner = new Scanner(System.in);
            try (Socket so = new Socket(SERVER_ADDRESS, PORT)) {
                DataInputStream entree = new DataInputStream(so.getInputStream());
                PrintWriter sortie = new PrintWriter(so.getOutputStream(), true);
    
                while(!debutJeu){
                    String reception = entree.readUTF();
                    System.out.println("Donnée reçue : " + reception);
                    readEntree(reception);
                }
                // Recevoir l'état initial du jeu
                System.out.println("En attente de données");
                String gameStateJson = entree.readUTF();


                

                System.out.println("Donnée recue");
                initGame(entree);
                SnakeGame snakeGame = mapper.readValue(gameStateJson, SnakeGame.class);
                System.out.println("État initial du jeu reçu: " + gameStateJson);
    
                // Thread 1 pour la saisie
                Thread threadSendMessage = new Thread(() -> {
                    sendAction(so, SERVER_ADDRESS, PORT, entree, sortie, scanner);
                });
                threadSendMessage.start();
    
                // Thread 2 (principal) pour l'ecoute du serveur
                while (!end) {
                    readUpdate(entree, sortie, end, so);
                }
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void readEntree(String reception){
            String[] receptionSplit = reception.split(":");
            String key = receptionSplit[0];
            switch (key) {
                case "NUMSNAKE":
                    playerIndex = Integer.valueOf(receptionSplit[1]);
                    System.out.println("playerIndex : " + playerIndex);
                    break;
                case "ETATINITIAL":
                    String gameStateJson = receptionSplit[1];
                    try {
                        snakeGame = mapper.readValue(gameStateJson, SnakeGame.class);
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } 
                    System.out.println("gameStateJson : \n" + gameStateJson);
                    break;
                case "DEBUTJEU":
                    debutJeu = true;
                default:
                    break;
            }
        }
    
        public static void sendAction(Socket so, String serveur, int port,
            DataInputStream entree, PrintWriter sortie, Scanner scanner) {
        while (!end) {
            System.out.println("Entrez une action (UP/DOWN/LEFT/RIGHT ou STOP pour quitter): ");
            String action = scanner.nextLine();
            sortie.println(action); // Envoi de l’action au serveur
    
            // Vérifier si l'action est "stop"
            if (action.equals("stop")) {
                sortie.println("stopOK"); // Signaler au serveur que le client veut arrêter
                end = true; // Terminer la boucle
                System.out.println("Fin du jeu.");
            }
    
            // Réception de l'état du jeu mis à jour
            String newStateJson = null;
            try {
                // Vérifier si des données sont disponibles avant de lire
                if (entree.available() > 0) {
                    newStateJson = entree.readUTF();
                    snakeGame = mapper.readValue(newStateJson, SnakeGame.class);
                    System.out.println("État mis à jour: " + newStateJson.toString());
    
                    // Mettre à jour l'affichage du jeu
                    // controller.updateGame(snakeGame);
                } else {
                    System.out.println("Aucune donnée disponible pour lire.");
                }
            } catch (EOFException e) {
                // Gérer la fin du flux
                System.out.println("Le serveur a fermé la connexion ou fin de flux atteinte.");
                end = true;  // Sortir de la boucle si la connexion est fermée
            } catch (IOException e) {
                // Autres erreurs IO
                System.out.println("Problème dans SendAction : ");
                e.printStackTrace();
            }
        }
    
        // Fermer les ressources une fois que la boucle est terminée
        try {
            if (so != null && !so.isClosed()) {
                so.close(); // Fermer le socket
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
        public static void initGame(DataInputStream entree) {
            String gameStateJson;
            try {
                if(entree.available() >0){
                gameStateJson = entree.readUTF(); // Lire l'état initial du jeu envoyé par le serveur
                snakeGame = mapper.readValue(gameStateJson, SnakeGame.class); // Convertir JSON en objet SnakeGame
                System.out.println("État initial du jeu reçu: " + gameStateJson);
    
                // Initialiser le jeu avec l'état reçu
                // controller.initializeGame(snakeGame); // Mettre à jour le contrôleur avec
                // l'état du jeu initial
                }
            } catch (IOException e) {
                System.out.println("Probleme dans initGame : ");
                e.printStackTrace();
            }
        }
    
        public static void readUpdate(DataInputStream entree, PrintWriter sortie, boolean end, Socket so) {
            try {
                if (!end) {
                    if (entree.available() > 0) {
                        // Lire l'état du jeu mis à jour envoyé par le serveur
                        String gameStateJson = entree.readUTF(); // Recevoir l'état du jeu mis à jour
                        snakeGame = mapper.readValue(gameStateJson, SnakeGame.class); // Convertir en objet SnakeGame
                    System.out.println("État mis à jour du jeu: " + snakeGame.toString());

                    // Mettre à jour l'affichage du jeu ou l'état interne du jeu ici
                    // controller.updateGame(snakeGame); // Utiliser le contrôleur pour mettre à
                    // jour l'affichage

                    // Confirmer la réception de la mise à jour
                    sortie.println("updateReceived");
                }
            }
        } catch (IOException e) {
            System.out.println("Problème de lecture : " + e.getMessage());
        }

        // Fermer proprement les flux et le socket lorsque le jeu est terminé
        if (end) {
            try {
                System.out.println("Fermeture de la connexion...");
                entree.close();
                sortie.close();
                so.close();
            } catch (IOException e) {
                System.out.println("Problème lors de la fermeture : " + e.getMessage());
            }
        }
    }

    /*@Override
    public void keyPressed(KeyEvent e) {
        for (Snake s : snake_liste) {
            if (s.strategie instanceof StrategieClavier) {

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_NUMPAD8:
                        if (s.strategie.isLegalMove(AgentAction.MOVE_UP))
                            s.agentAction = AgentAction.MOVE_UP;
                        break;
                    case KeyEvent.VK_NUMPAD5:
                        if (s.strategie.isLegalMove(AgentAction.MOVE_DOWN))
                            s.agentAction = AgentAction.MOVE_DOWN;
                        break;
                    case KeyEvent.VK_NUMPAD4:
                        if (s.strategie.isLegalMove(AgentAction.MOVE_LEFT))
                            s.agentAction = AgentAction.MOVE_LEFT;
                        break;
                    case KeyEvent.VK_NUMPAD6:
                        if (s.strategie.isLegalMove(AgentAction.MOVE_RIGHT))
                            s.agentAction = AgentAction.MOVE_RIGHT;
                        break;
                    default:
                        break;
                }
            }
        }

    }*/

    public static void main(String[] args) {
        new ClientSnake().start();
    }
}
