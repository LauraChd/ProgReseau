package reseau;

import java.net.*;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import snake.controller.ControllerSimpleGame;
import snake.controller.ControllerSnakeGame;
import snake.game.SnakeGame;
import snake.model.InputMap;

import java.io.*;

/* Application cliente du serveur servTexte1, syntaxe d’appel java cliTexte1 s p c, elle envoie la
chaîne de caractères c (terminée par un newline) à la machine s sur le port p et elle reçoit la
longueur de c sous la forme d’un entier */
public class Client {

    private static boolean end;
    private static final int PORT = 4567;
    private static ObjectMapper mapper = new ObjectMapper();
    private static SnakeGame snakeGame;
    /*
     * 1 = nom serveur
     * 2 = port serveur
     */

    public static void start(String serveur) {
        end = false;
        Scanner scanner = new Scanner(System.in);
        if (serveur != null) { // on récupère les paramètres
            try (Socket so = new Socket(serveur, PORT)) {
                // System.out.println("client est connecté");
                DataInputStream entree = new DataInputStream(so.getInputStream()); // besoin d'un bufferedReader ?
                PrintWriter sortie = new PrintWriter(so.getOutputStream(), true);

                initGame(entree);

                // Thread 1 pour la saisie
                Thread threadSendMessage = new Thread(() -> {
                    sendAction(so, serveur, PORT, entree, sortie, scanner);
                });
                threadSendMessage.start();

                // Thread 2 (principal) pour l'ecoute du serveur
                while (!end) {
                    readUpdate(entree, sortie, end, so);
                }

            } catch (UnknownHostException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println("Aucun serveur n’est rattaché au port ");
            }

        } else {
            System.out.println("syntaxe d’appel java cliTexte serveur port chaine_de_caractères\n");
        }
    }

    public static void main(String[] argu) {
        if (argu.length == 1) {
            new Client();
            Client.start(argu[0]);
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

}
