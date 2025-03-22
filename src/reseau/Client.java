package reseau;
import java.net.*;
import java.util.Scanner;

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

    /*
     * 1 = nom serveur
     * 2 = port serveur
     */
    public static void main(String[] argu) {
        DataInputStream entree;
        PrintWriter sortie;
        String serveur; // le serveur
        int port; // le port de connexion
        end = false;
        Scanner scanner = new Scanner(System.in);
        if (argu.length == 2) { // on récupère les paramètres
            serveur = argu[0]; // nom du serveur
            port = Integer.parseInt(argu[1]); // port
            try {
                // On connecte un socket
                Socket so = new Socket(serveur, port);
                System.out.println("so est connecté");
                File mapFile = receiveMap(so);
                displayMap(mapFile);
                System.out.println("Map recue");
                // init des entrées et sorties
                sortie = new PrintWriter(so.getOutputStream(), true);
                entree = new DataInputStream(so.getInputStream());
                // Thread 1 pour la saisie
                Thread threadSendMessage = new Thread(() -> {
                    sendMessage(so, serveur, argu, port, entree, sortie, scanner);
                });
                threadSendMessage.start();
                // Thread 2 (principal) pour l'ecoute du serveur
                while (!end) {
                    readMessage(entree, sortie, end, so);
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

    private static void displayMap(File mapFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                System.out.println(ligne);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de l'affichage de la map : " + e.getMessage());
        }
    }

    private static File receiveMap(Socket so) {
        try {
            DataInputStream in = new DataInputStream(so.getInputStream());
            File mapFile = new File("arenaNoWall.lay");
            FileOutputStream fileOut = new FileOutputStream(mapFile);

            // Lire la taille du fichier
            long fileSize = in.readLong();
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalRead = 0;

            // Lire et enregistrer le fichier
            while (totalRead < fileSize && (bytesRead = in.read(buffer)) > 0) {
                fileOut.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }

            fileOut.close();
            System.out.println("Map sauvegardée sous " + mapFile.getAbsolutePath());
            return mapFile;
        } catch (IOException e) {
            System.out.println("Erreur lors de la réception de la map : " + e.getMessage());
        }
        return null;
    }

    public static void sendMessage(Socket so, String serveur, String[] argu, int port,
            DataInputStream entree, PrintWriter sortie, Scanner scanner) {
        String input;
        // lecture de la chaîne
        System.out.println("Entrez le message à envoyer : (stop pour terminer la connexion)");
        input = scanner.nextLine();
        // on continue à communiquer tant que le socket est ouvert (c'est le serveur qui
        // le ferme en premier)
        while (!end) {
            if (input.equals("stop")) {
                end = true;
            }
            sortie.println(input); // str); // on écrit la chaîne et le newline dans le canal de sortie
            if (!end) {
                input = scanner.nextLine();
            }
        }
    }

    public static void readMessage(DataInputStream entree, PrintWriter sortie, boolean end, Socket so) {
        try {
            if (!end) {
                String message = entree.readUTF();
                System.out.println(message);
                if (message.equals("stop")) {
                    end = true;
                    sortie.println("stopOK");
                }
            } else {
                System.out.println("Fermeture de la communication");
                so.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startSnakeGame(String layoutPath) {
        SnakeGame snakeGame;
        try {
            snakeGame = new SnakeGame(10000, new InputMap(layoutPath));
            ControllerSnakeGame controllerSnakeGame = new ControllerSnakeGame(snakeGame, layoutPath);
            ControllerSimpleGame c1 = new ControllerSimpleGame(snakeGame);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
