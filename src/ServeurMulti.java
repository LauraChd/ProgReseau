import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/*programme serveur qui écoute sur le port p (passé en paramètre) et qui renvoie la longueur
de la chaîne de caractères que lui envoie un client. La chaîne envoyée se termine par un newline.*/

public class ServeurMulti {

    public static List<DataOutputStream> listClients = new ArrayList<>();

    public static void main(String[] argu) {
        int port; // le port d’écoute
        ServerSocket ecoute;
        Socket so;
        int numClient = 1;

        if (argu.length == 1) { // lance le serveur avec son port
            try {
                port = Integer.parseInt(argu[0]); // on récupère le port
                ecoute = new ServerSocket(port); // on crée le serveur
                System.out.println("Serveur mis en place ");

                while (true) {
                    try {
                        // Attend un client
                        so = ecoute.accept();
                        traiterClient(so, numClient);
                        numClient++;
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la communication avec un client : " + e.getMessage());
                    }
                }
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
                    // sortie.writeUTF(str);
                    shareMessage(str, sortie);
                    System.out.println("BLOQUE3");
                    // get la prochaine chaîne
                    // entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                    str = entree.readLine(); // on lit ce qui arrive
                    System.out.println("BLOQUE4");
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
        for (DataOutputStream sortie : listClients) {
            if (sortie != currentClient) {
                try {
                    sortie.writeUTF(str);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    /*
     * public static void (DataOutputStream sortie) {
     * try {
     * sortie.writeInt(0000000000000);
     * } catch (IOException e) {
     * // TODO Auto-generated catch block
     * e.printStackTrace();
     * }
     * }
     */
}
