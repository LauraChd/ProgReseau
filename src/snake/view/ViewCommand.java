package snake.view;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import snake.controller.AbstractController;
import snake.etat.Etat;
import snake.etat.EtatInitial;
import snake.game.Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class ViewCommand extends JPanel implements java.util.Observer{//la vue => observateur du jeu
    JFrame frame_commandes;
    JLabel label2;
    Game game;//observable
    public AbstractController abstractController;
    private Etat etat_du_jeu;
    JButton button1;
    JButton button2;
    JButton button3;
    JButton button4;
    JFrame jFrame;
    
    public ViewCommand(AbstractController abstractController, Observable game){
        this.abstractController = abstractController;
        game.addObserver(this);
        this.game = (Game) game;
        etat_du_jeu = new EtatInitial(this);

        jFrame = new JFrame();
        jFrame.setTitle("Commande");
        jFrame.setSize(new Dimension(1000, 450));
        Dimension windowSize = jFrame.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = windowSize.height * 2;
        jFrame.setLocation(dx, dy);

        JPanel panel1 = new JPanel(new GridLayout(2, 1));//global
        JPanel panel2 = new JPanel(new GridLayout(1, 4));//situé en haut
        JPanel panel3 = new JPanel(new GridLayout(1, 2));//situé en bas

        //PARTIE DU HAUT
        Icon icone_restart = new ImageIcon("./src/snake/icons/icon_restart.png");
        Icon icone_play = new ImageIcon("./src/snake/icons/icon_play.png");
        Icon icone_step = new ImageIcon("./src/snake/icons/icon_step.png");
        Icon icone_pause = new ImageIcon("./src/snake/icons/icon_pause.png");

        button1 = new JButton(icone_restart);
        button2 = new JButton(icone_play);
        button3 = new JButton(icone_step);
        button4 = new JButton(icone_pause);

        panel2.add(button1);
        panel2.add(button2);
        panel2.add(button3);
        panel2.add(button4);

        panel1.add(panel2);

        //PARTIE DU BAS
        //partie gauche
        JPanel panel3_sep = new JPanel(new GridLayout(2, 1));

        JLabel label1 = new JLabel("Number of turns per second" ,JLabel.CENTER);
        JSlider slider1 = new JSlider(1, 10, 1);

        slider1.setPaintTrack(true); 
        slider1.setPaintTicks(true); 
        slider1.setPaintLabels(true); 

        slider1.setMajorTickSpacing(1); 
        slider1.setMinorTickSpacing(1); 

        panel3_sep.add(label1);
        panel3_sep.add(slider1);
        panel3.add(panel3_sep);

        //partie droite
        JLabel label2 = new JLabel("Turn : " + this.game.turn,JLabel.CENTER);
        this.label2 = label2;
        panel3.add(label2);

        panel1.add(panel3);

        jFrame.add(panel1);

        button1.setEnabled(false);
        button4.setEnabled(false);


        button1.addActionListener(new ActionListener() {//bouton restart
            public void actionPerformed(ActionEvent e) {
                slider1.setValue(1);
                etat_du_jeu.restart();
            }
        });

        button2.addActionListener(new ActionListener() {//bouton play
            public void actionPerformed(ActionEvent e) {
                etat_du_jeu.play();
            }
        });

        button3.addActionListener(new ActionListener() {//bouton step
            public void actionPerformed(ActionEvent e) {
                etat_du_jeu.step();
            }
        });

        button4.addActionListener(new ActionListener() {//bouton pause
            public void actionPerformed(ActionEvent e) {
                etat_du_jeu.pause();
            }
        });

        slider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                etat_du_jeu.setSpeed(((JSlider) ce.getSource()).getValue());
            }
        });

        jFrame.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        label2.setText("Turn : " + game.turn);
    }

    public void setEtat(Etat newEtat){
        this.etat_du_jeu = newEtat;
    }

    public void updateButtons(boolean b1, boolean b2, boolean b3, boolean b4) {
        button1.setEnabled(b1);
        button2.setEnabled(b2);
        button3.setEnabled(b3);
        button4.setEnabled(b4);
    }

    public Etat getEtat_du_jeu() {
        return etat_du_jeu;
    }

    public void fermerFenetre() {
        jFrame.dispose();
    }
    

}
