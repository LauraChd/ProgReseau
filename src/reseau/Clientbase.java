package reseau;

import java.net.*;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import snake.controller.ControllerSnakeGame;
import snake.game.GameState;
import snake.game.SnakeGame;
import snake.model.InputMap;

import java.io.*;

public class Clientbase {
    private static boolean end = false;
    private static String layoutPath = "src/snake/layouts/arenaNoWall2.lay";
    private static GameState gState = new GameState();
    private static SnakeGame snakeGame;


    public static void main(String[] argu) {
        if (argu.length != 2) {
            System.out.println("Syntaxe : java Clientbase serveur port");
            return;
        }

        String serveur = argu[0];
        int port = Integer.parseInt(argu[1]);

        try {
            Socket so = new Socket(serveur, port);
            PrintWriter sortie = new PrintWriter(so.getOutputStream(), true);
            BufferedReader entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            System.out.println("Connexion réussie au serveur " + serveur + " sur le port " + port);
            ControllerSnakeGame controllerSnakeGame = null;
            try {
                snakeGame = new SnakeGame(10000, new InputMap(layoutPath));
                controllerSnakeGame = new ControllerSnakeGame(snakeGame, layoutPath);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Thread pour envoyer des messages
            Thread threadSendMessage = new Thread(() -> sendMessage(sortie, scanner));
            threadSendMessage.start();

            // Thread principal pour recevoir les messages
            while (!end) {
                String message = entree.readLine();
                if (message != null) {
                    System.out.println(message);
                    if(message.contains("ETATINIT") ){ //soit newtstae ici, soit dans autre if
                        String gameStateJSON = message.split("ETATINIT")[1];
                        try {
                            GameState gState = GameState.fromJson(gameStateJSON);
                            System.out.println();
                            snakeGame.updateGameFromGameState(gState);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (message.equals("stopOK")) {
                        end = true;
                        System.out.println("Fermeture de la communication...");
                        so.close();
                    }
                    if(message.contains("NEWSTATE")){
                        String gameStateJSON = message.split("NEWSTATE")[1];
                        System.out.println(gameStateJSON);
                        try {
                            GameState gState = GameState.fromJson(gameStateJSON);
                            System.out.println();
                            snakeGame.updateGameFromGameState(gState);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if(message.contains("PLAY")){
                        if(controllerSnakeGame != null)
                            controllerSnakeGame.getViewCommand().getEtat_du_jeu().play();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    public static void sendMessage(PrintWriter sortie, Scanner scanner) {
        while (!end) {
            String input = scanner.nextLine();

            sortie.println(input);
            if (input.equals("stop")) {
                end = true;
            }
        }
    }
}
