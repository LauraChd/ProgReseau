package snake.view;
import javax.swing.JPanel;

import snake.game.SnakeGame;

import java.awt.BorderLayout;
import java.awt.Dimension;

import snake.utils.FeaturesSnake;
import snake.utils.FeaturesItem;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class ViewSnakeGame extends JPanel implements Observer {
    PanelSnakeGame panelSnakeGame;

    public ViewSnakeGame(PanelSnakeGame panelSnakeGame, SnakeGame game) {
        this.panelSnakeGame = panelSnakeGame;
        game.addObserver(this);

        this.setLayout(new BorderLayout());
        this.add(panelSnakeGame, BorderLayout.CENTER);

        this.setPreferredSize(new Dimension(panelSnakeGame.getSizeX() * 50, panelSnakeGame.getSizeY()*50));
        this.setVisible(true);
    }

    public void updateView(ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items) {
        panelSnakeGame.updateInfoGame(snakes, items);
        panelSnakeGame.repaint();
    }

    @Override
    public void update(Observable game, Object snakegame) {
        //System.out.println("méthode update");
        updateView(((SnakeGame)game).getFeaturesSnakes(),((SnakeGame)game).getFeaturesItems());
    }

    public void updatePanel(PanelSnakeGame newPanel) {
        this.removeAll();
        this.panelSnakeGame = newPanel;
        this.setLayout(new BorderLayout());
        this.add(panelSnakeGame, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }


    
}
