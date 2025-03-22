package reseau;
import java.net.*;

import snake.controller.ControllerSimpleGame;
import snake.controller.ControllerSnakeGame;
import snake.game.SnakeGame;
import snake.model.InputMap;

import java.io.*;

/*programme serveur qui écoute sur le port p (passé en paramètre) et qui renvoie la longueur
de la chaîne de caractères que lui envoie un client. La chaîne envoyée se termine par un newline.*/

public class Serveur {

    public static void main(String[] argu) throws Exception{
        int port; // le port d’écoute
        ServerSocket ecoute;
        Socket so;
        BufferedReader entree;
        DataOutputStream sortie;
        String str; // la chaîne reçue
        if (argu.length == 1) {
            try {
                port = Integer.parseInt(argu[0]); // on récupère le port
                ecoute = new ServerSocket(port); // on crée le serveur
                System.out.println("serveur mis en place ");
                System.out.println("nom du serveur : " + ecoute.getInetAddress().getHostName());
                //MISE EN ROUTE DU JEU 
                String layoutPath = "src/snake/layouts/arenaNoWall.lay"; 

                SnakeGame snakeGame = new SnakeGame(10000,new InputMap(layoutPath));
                //@SuppressWarnings("unused")
                ControllerSnakeGame controllerSnakeGame = new ControllerSnakeGame(snakeGame, layoutPath);
                //@SuppressWarnings("unused")
                ControllerSimpleGame c1 = new ControllerSimpleGame(snakeGame);

                //ATTENTE DE CLIENTS
                while (true) {// le serveur va attendre qu’une connexion arrive
                    so = ecoute.accept();
                    System.out.println("Une nouvelle connexion a été ouverte");
                    entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                    sortie = new DataOutputStream(so.getOutputStream());
                    str = entree.readLine(); // on lit ce qui arrive
                    //on continue la communication tant que la chaîne recue n'est pas "stop"
                    while(!str.equals("stop")){
                        System.out.println("on a reçu : |" + str + "|");
                        sortie.writeInt(str.length()); // on renvoie la longueur de la chaîne
                        //get la prochaine chaîne
                        //entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                        str = entree.readLine(); // on lit ce qui arrive
                    }
                    so.close();
                    System.out.println("La connexion a été fermée");
                    
                }
            } catch (IOException e) {
                System.out.println("problème\n" + e);
            }
        } else {
            System.out.println("syntaxe d’appel java servTexte port\n");
        }
    }

    
}
