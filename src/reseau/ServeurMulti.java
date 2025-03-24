package reseau;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import snake.game.SnakeGame;
import snake.model.InputMap;

import java.io.*;

/*programme serveur qui écoute sur le port p (passé en paramètre) et qui renvoie la longueur
de la chaîne de caractères que lui envoie un client. La chaîne envoyée se termine par un newline.*/

public class ServeurMulti {

    private static final int MAX_CLIENTS = 2;
    private static final int PORT = 4567;
    private static SnakeGame snakeGame;
        private static ObjectMapper mapper = new ObjectMapper();
            private static List<Socket> clients = new ArrayList<>();
            private static List<DataOutputStream> listClients = new ArrayList<>();
        
            public ServeurMulti() {
                String layoutPath = "src/snake/layouts/alone.lay";
                try {
                    this.snakeGame = new SnakeGame(10000, new InputMap(layoutPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
            public static void startServeur() {
                int numClient = 1;
        
                try (ServerSocket ecoute = new ServerSocket(PORT)) {
                    System.out.println("Serveur en attente de " + MAX_CLIENTS + " connexions");
        
                    while (clients.size() < MAX_CLIENTS) {
                        try {
                            // Attend un client
                            Socket so = ecoute.accept();
                            clients.add(so);
                            int currentNumClient = numClient;
                            new Thread(() -> traiterClient(so, currentNumClient)).start();
                            numClient++;
                        } catch (IOException e) {
                            System.out.println("Erreur lors de la communication avec un client : " + e.getMessage());
                        }
                    }
                    System.out.println("DEUX CLIENTS CONNECTES");
        
                } catch (IOException e) {
                    System.out.println("Erreur lors de la mise en place du serveur : " + e.getMessage());
                }
        
            }
        
            public static void main(String[] argu) {
                if (argu.length == 0) {
                    new ServeurMulti();
                    ServeurMulti.startServeur();
                }
            }
        
            public static void traiterClient(Socket so, int numClient) {
                try {
                    System.out.println(numClient + ". Une nouvelle connexion a été ouverte");
            
                    BufferedReader entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                    DataOutputStream sortie = new DataOutputStream(so.getOutputStream());
            
                    listClients.add(sortie);
            
                    // Envoyer l'état initial du jeu
                    String gameStateJson = mapper.writeValueAsString(snakeGame); // Convertir SnakeGame en JSON
            sortie.writeUTF(gameStateJson); // Envoyer l'état du jeu initial au client
    
            String str = entree.readLine(); // on lit ce qui arrive
            while (!str.equals("stop")) {
                System.out.println(numClient + ". On a reçu : |" + str + "|");
                sendUpdate(str, sortie);  // Partager les messages avec tous les clients
                str = entree.readLine();  // Lire la prochaine chaîne
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
        List<DataOutputStream> listClients = ServeurMulti.listClients;
        for (int i = 0; i < listClients.size(); i++) { 
            DataOutputStream sortie = listClients.get(i);
            if (sortie != currentClient) {
                try {
                    // Convertir SnakeGame mis à jour en JSON
                    String updatedGameStateJson = mapper.writeValueAsString(snakeGame);
                    sortie.writeUTF(updatedGameStateJson);  // Envoyer l'état du jeu mis à jour
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    

}
