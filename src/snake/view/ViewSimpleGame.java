package snake.view;
import javax.swing.*;

import snake.game.Game;

import java.awt.*;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class ViewSimpleGame implements java.util.Observer{//la vue => observateur du jeu
    JFrame frame_tour;
    JLabel label;
    JLabel label_2;
    Game game;

    public ViewSimpleGame(Game game){
        this.game = game;
        game.addObserver(this);
        JFrame jFrame = new JFrame();
        jFrame.setTitle("Game");
        jFrame.setSize(new Dimension(400, 400));
        Dimension windowSize = jFrame.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2 + 400;
        int dy = centerPoint.y - windowSize.height / 2 - 200;
        jFrame.setLocation(dx, dy);
        
        String texte = "Tour : " + game.turn;
        JLabel label = new JLabel(texte,JLabel.CENTER);
        this.label = label;
        jFrame.add(label);

        jFrame.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        String texte = "Tour : " + game.turn;
        if(game.gameContinue() == false){
            texte = "Jeu fini au tour " + game.turn;
        }
        label.setText(texte);
    }
}
