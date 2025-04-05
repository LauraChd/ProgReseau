package reseau;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import snake.controller.ControllerSnakeGame;
import snake.game.GameState;
import snake.game.SnakeGame;
import snake.model.InputMap;
import snake.utils.AgentAction;

import java.io.*;

public class ServeurMultibase {

    public static List<PrintWriter> listClients = new ArrayList<>();
    private static String layoutPath = "src/snake/layouts/arenaNoWall.lay";
    private static List<Socket> clients = new ArrayList<>();
    private static final int MAX_CLIENTS = 2;
    private static ObjectMapper mapper = new ObjectMapper();
    private static final int PORT = 4567;
    private static GameState myGS = null;
    private static SnakeGame snakeGame;
    private static ControllerSnakeGame controllerSnakeGame;
    private static String myGSString;

    public static void main(String[] argu) {
        // int port;
        ServerSocket ecoute;
        Socket so;
        int numClient = 1;

        if (argu.length == 0) {
            try {
                // port = Integer.parseInt(argu[0]);
                ecoute = new ServerSocket(PORT);
                System.out.println("Serveur mis en place sur le port " + PORT);

                while (clients.size() < MAX_CLIENTS) {
                    try {
                        so = ecoute.accept();
                        clients.add(so);
                        traiterClient(so, numClient);
                        numClient++;
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la communication avec un client : " + e.getMessage());
                    }
                }
                System.out.println("DEUX CLIENTS CONNECTES");
                try {
                    snakeGame = new SnakeGame(10000, new InputMap(layoutPath));
                    controllerSnakeGame = new ControllerSnakeGame(snakeGame, layoutPath);
                    controllerSnakeGame.getViewCommand().fermerFenetre();
                    //maj du jeu du serveur avec l'état init
                    String gameStateJson = mapper.writeValueAsString(snakeGame);
                    myGS = GameState.fromJson(gameStateJson);
                    snakeGame.updateGameFromGameState(myGS);
                    //envoi aux clients les etats init
                    shareMessageAll("ETATINIT" + gameStateJson);
                    System.out.println("first" + gameStateJson);
                    shareMessageAll("PLAY");
                    controllerSnakeGame.getViewCommand().getEtat_du_jeu().play();
                    //envoi toutes les 500 milli le nouvel état aux joueurs
                    Thread thread = new Thread(() -> {
                        while (true) {
                            try {
                                myGSString = mapper.writeValueAsString(snakeGame);
                                myGS = GameState.fromJson(myGSString);
                                snakeGame.updateGameFromGameState(myGS);

                            } catch (JsonProcessingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            
                            shareMessageAll("NEWSTATE" + myGSString);
                
                            try {
                                Thread.sleep(500); 
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    //controllerSnakeGame.getViewCommand().getEtat_du_jeu().play();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la mise en place du serveur : " + e.getMessage());
            }
        } else {
            System.out.println("Syntaxe d’appel : java ServeurMultibase port");
        }
    }

    public static void traiterClient(Socket so, int numClient) {
        Thread thread = new Thread(() -> {
            try {
                BufferedReader entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
                PrintWriter sortie = new PrintWriter(so.getOutputStream(), true);
                System.out.println(numClient + ". Une nouvelle connexion a été ouverte");
                ServeurMultibase.listClients.add(sortie);

                String str;
                while ((str = entree.readLine()) != null) {
                    if (str.equals("stop")) {
                        System.out.println(numClient + ". Fin de communication demandée");
                        sortie.println("stopOK");
                        break;
                    }

                    System.out.println(numClient + ". Message reçu : |" + str + "|"); //TODO gérer les nouveaux objets qui apparaissent en +
                    AgentAction aa = null;
                    switch (str) {
                        case "UP":
                            aa = AgentAction.MOVE_UP;
                            break;
                        case "DOWN":
                            aa = AgentAction.MOVE_DOWN;
                            break;
                        case "RIGHT":
                            aa = AgentAction.MOVE_RIGHT;
                            break;
                        case "LEF":
                            aa = AgentAction.MOVE_LEFT;
                            break;
                    
                        default:
                            break;
                    }
                    snakeGame.getSnake_liste().get(numClient-1).setAgentAction(aa); //TODO à voir si ça maj bien, sion dois récup liste, maj puis reset
                    myGSString = mapper.writeValueAsString(snakeGame);
                    //TODO transformer en JSON et envoyer le JSON
                    //+ maj du game state
                    try {
                        myGS = GameState.fromJson(myGSString);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    snakeGame.updateGameFromGameState(myGS);
                    //shareMessage(str, sortie);
                }

                System.out.println(numClient + ". Connexion fermée");
                sortie.close();
                entree.close();
                so.close();
                ServeurMultibase.listClients.remove(sortie);

            } catch (IOException e) {
                System.out.println(numClient + ". Problème : " + e.getMessage());
            }
        });
        thread.start();
    }

    // public static void shareMessage(int numClient, String str) {

    public static void shareMessage(String str, PrintWriter currentClient) {
        List<PrintWriter> listClients = ServeurMultibase.listClients;
        for (int i = 0; i < listClients.size(); i++) { // DataOutputStream sortie : listClients) {
            PrintWriter sortie = listClients.get(i);
            if (sortie != currentClient) {
                sortie.println("Client " + i + " => " + str);

            }
        }
    }

    public static void shareMessageAll(String str) {
        List<PrintWriter> listClients = ServeurMultibase.listClients;
        for (int i = 0; i < listClients.size(); i++) { // DataOutputStream sortie : listClients) {
            PrintWriter sortie = listClients.get(i);
            sortie.println(str);

        }
    }
}
