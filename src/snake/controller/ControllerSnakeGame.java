package snake.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowFocusListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import snake.game.SnakeGame;
import snake.model.InputMap;
import snake.view.PanelSnakeGame;
import snake.view.ViewCommand;
import snake.view.ViewSnakeGame;

public class ControllerSnakeGame extends AbstractController {
    private JFrame frame;
    private SnakeGame snakeGame;
    @SuppressWarnings("unused")
    private ViewCommand viewCommand;
    private String currentLayoutPath;

    public ControllerSnakeGame(SnakeGame snakeGame, String layoutPath) {
        super(snakeGame);
        this.snakeGame = snakeGame;
        this.currentLayoutPath = layoutPath;

        this.viewCommand = new ViewCommand(this, snakeGame);

        fenetre(currentLayoutPath);
    }

    private void fenetre(String layoutPath) {
        try {
            if (frame != null) {//fermeture propre de la fenêtre si elle existe déjà
                frame.dispose();
            }

            InputMap inputMap = new InputMap(layoutPath);

            snakeGame.carte = inputMap;
            snakeGame.initializeGame();

            PanelSnakeGame panelSnakeGame = new PanelSnakeGame(
                    inputMap.getSizeX(),
                    inputMap.getSizeY(),
                    inputMap.get_walls(),
                    inputMap.getStart_snakes(),
                    inputMap.getStart_items()
            );

            
            ViewSnakeGame viewSnakeGame = new ViewSnakeGame(panelSnakeGame, snakeGame);

            frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //menu
            JMenuBar menuBar = new JMenuBar();
            JComboBox<String> layoutComboBox = new JComboBox<>(getLayoutFiles());
            layoutComboBox.setSelectedItem(new File(layoutPath).getName());//sélectionne l'élément du menu correspondant au layout actuel
            menuBar.add(layoutComboBox);
            frame.setJMenuBar(menuBar);

            layoutComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedLayout = (String) layoutComboBox.getSelectedItem();
                    if (selectedLayout != null) {
                        String newLayoutPath = "src/snake/layouts/" + selectedLayout;
                        currentLayoutPath = newLayoutPath;
                        fenetre(newLayoutPath);//recréé fenêtre avec le nouveau layout
                    }
                }
            });
            
            frame.add(viewSnakeGame);

            viewSnakeGame.addKeyListener(snakeGame);//pour action clavier
            viewSnakeGame.setFocusable(true);
            viewSnakeGame.requestFocusInWindow();

            //focus sur jeu et pas sur menu
            frame.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(java.awt.event.WindowEvent e) {
                    if (viewSnakeGame.isFocusable()) {
                        viewSnakeGame.requestFocusInWindow();
                    }
                }

                @Override
                public void windowLostFocus(java.awt.event.WindowEvent e) {
                }
            });

            //remet fenêtre jeu au 1e plan
            Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //si la fenêtre n'a pas le focus on la met au premier plan (toutes les 100 ms)
                    //(sinon ce n'est pas pratique car il faut recliquer sur la fenêtre après avoir cliqué sur une autre pour démarrer le jeu par exemple, donc ça permet que ce soit plus pratique au niveau des actions claviers)
                    if (!frame.isFocused()) {
                        frame.toFront();
                        SwingUtilities.invokeLater(() -> {
                            viewSnakeGame.requestFocusInWindow();
                        });
                    }
                }
            });

            timer.start();
            
            frame.pack();
            frame.setVisible(true);

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de la carte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    //récupère liste fichiers layout disponibles dans le dossier layouts
    private String[] getLayoutFiles() {
        File layoutDirectory = new File("src/snake/layouts");
        String[] layouts = layoutDirectory.list((dir, name) -> name.endsWith(".lay"));
        if (layouts == null || layouts.length == 0) {
            return new String[]{"Aucun layout trouvé"};
        }
        return layouts;
    }
}