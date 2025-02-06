package snake.game;

import java.util.ArrayList;

import snake.utils.AgentAction;
import snake.utils.ColorSnake;
import snake.utils.Position;

public class Snake {
    int taille;
    ArrayList<Position> position;
    public ColorSnake couleur;
    public AgentAction agentAction;
    public Strategie strategie;
    boolean is_invincible;
    boolean is_sick;
    int tour_malade_restant;
    int tour_invincible_restant;

    public Snake(ArrayList<Position> position,ColorSnake couleur){
        this.taille = 1;
        this.position = position;
        this.couleur = couleur;
        this.agentAction = AgentAction.MOVE_RIGHT;
        this.is_invincible = false;
        this.is_sick = false;
        this.tour_malade_restant = 0;
        this.tour_invincible_restant = 0;
    }

    

    public void moveAgent(AgentAction agentAction,int carteX,int carteY) {

        if (strategie.isLegalMove(agentAction)) {
            this.agentAction = agentAction;
        }
            
        //déplacement tête
        Position tete = position.get(0);
        Position nouvelle_tete = new Position(tete.getX(), tete.getY());
        
        if (this.agentAction == AgentAction.MOVE_UP) {
            nouvelle_tete.setY(tete.getY() - 1);  
        } else if (this.agentAction == AgentAction.MOVE_DOWN) {
            nouvelle_tete.setY(tete.getY() + 1); 
        } else if (this.agentAction == AgentAction.MOVE_LEFT) {
            nouvelle_tete.setX(tete.getX() - 1);  
        } else if (this.agentAction == AgentAction.MOVE_RIGHT) {
            nouvelle_tete.setX(tete.getX() + 1); 
        }

        //si tête hors limites => elle revient de l'autre côté
        nouvelle_tete.setX((nouvelle_tete.getX() + carteX) % carteX);
        nouvelle_tete.setY((nouvelle_tete.getY() + carteY) % carteY);

        position.add(0, nouvelle_tete);
        position.remove(position.size() - 1);

    }

    public void augmenter_taille() {
        if (position.size() == 1) {
            Position tete = position.get(0);
    
            if (agentAction == AgentAction.MOVE_UP) {
                position.add(new Position(tete.getX(), tete.getY() + 1));
            } else if (agentAction == AgentAction.MOVE_DOWN) {
                position.add(new Position(tete.getX(), tete.getY() - 1));
            } else if (agentAction == AgentAction.MOVE_LEFT) {
                position.add(new Position(tete.getX() + 1, tete.getY()));
            } else if (agentAction == AgentAction.MOVE_RIGHT) {
                position.add(new Position(tete.getX() - 1, tete.getY()));
            }
    
            taille++;
            return;
        }
    
        //si taille serpent > 1 
        Position fin = position.get(position.size() - 1);
        Position avant_fin = position.get(position.size() - 2);
    
        int X = fin.getX() - avant_fin.getX();
        int Y = fin.getY() - avant_fin.getY();
    
        Position nouveau_corp = new Position(fin.getX() + X, fin.getY() + Y);
    
        position.add(nouveau_corp);
        taille++;
    }
    
    


}
