import java.net.*;
import java.util.Scanner;
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
                System.out.println("Entrez le message à envoyer : (stop pour terminer la connexion)");
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
}
