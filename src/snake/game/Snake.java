package snake.game;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonValue;

import snake.utils.AgentAction;
import snake.utils.ColorSnake;
import snake.utils.Position;

public class Snake {
    int score;
    int taille;
    ArrayList<Position> position;
    public ColorSnake couleur;
    public AgentAction agentAction;
    public Strategie strategie;
    boolean is_invincible;
    boolean is_sick;
    int tour_malade_restant;
    int tour_invincible_restant;


    public Snake(){}

    public Snake(ArrayList<Position> position,ColorSnake couleur){
        this.taille = 1;
        this.position = position;
        this.couleur = couleur;
        this.agentAction = AgentAction.MOVE_RIGHT;
        this.is_invincible = false;
        this.is_sick = false;
        this.tour_malade_restant = 0;
        this.tour_invincible_restant = 0;
        this.score = 0;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int sc){
        this.score = sc;
    }

    public void incrScore(int plus){
        this.score += plus;
        System.out.println("SCORE " + score);
    }
    
    

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    public ArrayList<Position> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Position> position) {
        this.position = position;
    }

    public ColorSnake getCouleur() {
        return couleur;
    }

    public void setCouleur(ColorSnake couleur) {
        this.couleur = couleur;
    }

    public AgentAction getAgentAction() {
        return agentAction;
    }

    public void setAgentAction(AgentAction agentAction) {
        this.agentAction = agentAction;
    }

    public Strategie getStrategie() {
        return strategie;
    }

    public void setStrategie(Strategie strategie) {
        this.strategie = strategie;
    }

    public boolean isIs_invincible() {
        return is_invincible;
    }

    public void setIs_invincible(boolean is_invincible) {
        this.is_invincible = is_invincible;
    }

    public boolean isIs_sick() {
        return is_sick;
    }

    public void setIs_sick(boolean is_sick) {
        this.is_sick = is_sick;
    }

    public int getTour_malade_restant() {
        return tour_malade_restant;
    }

    public void setTour_malade_restant(int tour_malade_restant) {
        this.tour_malade_restant = tour_malade_restant;
    }

    public int getTour_invincible_restant() {
        return tour_invincible_restant;
    }

    public void setTour_invincible_restant(int tour_invincible_restant) {
        this.tour_invincible_restant = tour_invincible_restant;
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

    @Override
    public String toString() {
        return "Snake{" +
                "score=" + score +
                ", position=" + position +
                ", couleur=" + couleur +
                ", etat=" + (is_invincible ? "invincible" : (is_sick ? "sick" : "null")) +
                '}';
    }
    
    


}
