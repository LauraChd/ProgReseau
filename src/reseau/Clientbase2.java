package reseau;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class Clientbase2 {
    private static boolean end = false;
    private static PrintWriter sortie;

    public static void main(String[] argu) {
        if (argu.length != 2) {
            System.out.println("Syntaxe : java Clientbase serveur port");
            return;
        }

        String serveur = argu[0];
        int port = Integer.parseInt(argu[1]);

        try (
            Socket so = new Socket(serveur, port);
            PrintWriter writer = new PrintWriter(so.getOutputStream(), true);
            BufferedReader entree = new BufferedReader(new InputStreamReader(so.getInputStream()))
        ) {
            sortie = writer;
            System.out.println("Connexion rÃ©ussie au serveur " + serveur + " sur le port " + port);

            // CrÃ©ation d'une interface graphique pour capter les touches
            JFrame frame = new JFrame("Client ContrÃ´leur");
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (!end) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_Z -> sortie.println("haut");
                            case KeyEvent.VK_S -> sortie.println("bas");
                            case KeyEvent.VK_Q -> sortie.println("gauche");
                            case KeyEvent.VK_D -> sortie.println("droite");
                            case KeyEvent.VK_ESCAPE -> {
                                sortie.println("stop");
                                end = true;
                                System.out.println("Fermeture du client...");
                                System.exit(0);
                            }
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {}

                @Override
                public void keyTyped(KeyEvent e) {}
            });

            // Thread pour recevoir les messages du serveur
            while (!end) {
                String message = entree.readLine();
                if (message != null) {
                    System.out.println("Serveur: " + message);
                    if (message.equals("stopOK")) {
                        end = true;
                        System.out.println("Fermeture de la communication...");
                        so.close();
                        System.exit(0);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }
}
