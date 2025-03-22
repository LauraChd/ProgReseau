package reseau;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/*programme serveur qui écoute sur le port p (passé en paramètre) et qui renvoie la longueur
de la chaîne de caractères que lui envoie un client. La chaîne envoyée se termine par un newline.*/

public class ServeurMulti {

    private static final int MAX_CLIENTS = 2;
    private static List<Socket> clients = new ArrayList<>();
    private static List<DataOutputStream> listClients = new ArrayList<>();

    public static void main(String[] argu) {
        int port; // le port d’écoute
        ServerSocket ecoute;
        int numClient = 1;

        if (argu.length == 1) { // lance le serveur avec son port
            try {
                port = Integer.parseInt(argu[0]); // on récupère le port
                ecoute = new ServerSocket(port); // on crée le serveur
                System.out.println("Serveur en attente de " + MAX_CLIENTS + " connexions");

                while (clients.size() < MAX_CLIENTS) {
                    try {
                        // Attend un client
                        Socket so = ecoute.accept();
                        clients.add(so);
                        traiterClient(so, numClient);
                        numClient++;
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la communication avec un client : " + e.getMessage());
                    }
                }
                System.out.println("DEUX CLIENTS CONNECTES");
                sendMapToClients();

            } catch (IOException e) {
                System.out.println("Erreur lors de la mise en place du serveur : " + e.getMessage());
            }
        } else {
            System.out.println("Syntaxe d’appel : java servTexte port\n");
        }
    }

    public static void traiterClient(Socket so, int numClient) {
        Thread thread = new Thread(() -> {
            BufferedReader entree;
            DataOutputStream sortie;
            String str; // la chaîne reçue
            try {
                System.out.println(numClient + ". Une nouvelle connexion a été ouverte");
                entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                sortie = new DataOutputStream(so.getOutputStream());
                ServeurMulti.listClients.add(sortie);
                str = entree.readLine(); // on lit ce qui arrive
                // on continue la communication tant que la chaîne recue n'est pas "stop"
                while (!str.equals("stop")) {
                    System.out.println(numClient + ". On a reçu : |" + str + "|");
                    shareMessage(str, sortie);
                    // get la prochaine chaîne
                    str = entree.readLine(); // on lit ce qui arrive
                }
                while (!str.equals("stopOK")) {
                    System.out.println(numClient + ". Fin de communication demandée");
                    System.out.println("str de stop = " + str);
                    sortie.writeUTF(str);
                    str = entree.readLine(); // on lit ce qui arrive
                }
                // so.close();
                System.out.println(numClient + ". La connexion a été fermée");
            } catch (IOException e) {
                System.out.println(numClient + ". Problème\n" + e);
            }
        });
        thread.start();
    }

    public static void shareMessage(String str, DataOutputStream currentClient) {
        List<DataOutputStream> listClients = ServeurMulti.listClients;
        for (int i = 0; i < listClients.size(); i++) { // DataOutputStream sortie : listClients) {
            DataOutputStream sortie = listClients.get(i);
            if (sortie != currentClient) {
                try {
                    String strTemp = i + " => " + str;
                    sortie.writeUTF(strTemp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void sendMapToClients() {
        String layoutPath = "src/snake/layouts/arenaNoWall.lay";
        File mapFile = new File(layoutPath);

        if (!mapFile.exists()) {
            System.out.println("Erreur : fichier de map introuvable !");
            return;
        }

        for (Socket client : clients) {
            try {
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                FileInputStream fileIn = new FileInputStream(mapFile);

                // Envoyer la taille du fichier en premier
                out.writeLong(mapFile.length());

                // Envoyer le contenu du fichier
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
                fileIn.close();

                System.out.println("Map envoyée au client.");

            } catch (IOException e) {
                System.out.println("Erreur lors de l'envoi de la map : " + e.getMessage());
            }
        }
    }
}
