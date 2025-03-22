package snake.game;

import snake.utils.AgentAction;
import snake.utils.ItemType;
import snake.utils.Position;

public class StrategieIA implements Strategie {

    Snake snake;
    
    public StrategieIA(){}

    public StrategieIA(Snake snake){
        this.snake = snake;   
    }

    @Override
    public boolean isLegalMove(AgentAction agentAction) {
        if(agentAction != null && snake.position.size() != 1){
            if(agentAction == AgentAction.MOVE_UP && snake.agentAction != AgentAction.MOVE_DOWN){
                return true;
            }
            else if(agentAction == AgentAction.MOVE_DOWN && snake.agentAction != AgentAction.MOVE_UP){
                return true;
            }
            else if(agentAction == AgentAction.MOVE_LEFT && snake.agentAction != AgentAction.MOVE_RIGHT){
                return true;
            }
            else if(agentAction == AgentAction.MOVE_RIGHT && snake.agentAction != AgentAction.MOVE_LEFT){
                return true;
            }
            else return false;
        }
        else 
            return true;
        
    }

    @Override
    public AgentAction direction_choisie(SnakeGame snakeGame) {
        int teteX = snake.position.get(0).getX();
        int teteY = snake.position.get(0).getY();

        int pommeX = 0;
        int pommeY = 0;

        for(Item item : snakeGame.item_liste){
            if (item.itemType == ItemType.APPLE){
                pommeX = item.x;
                pommeY = item.y;
            }
        }

        //actions possibles et leur distance de la pomme
        AgentAction[] actions = {AgentAction.MOVE_UP, AgentAction.MOVE_DOWN, AgentAction.MOVE_LEFT, AgentAction.MOVE_RIGHT};
        int minDistance = snakeGame.carte.getSizeX()*snakeGame.carte.getSizeY();
        AgentAction meilleure_action = snake.agentAction;//direction actuelle par défaut

        for (AgentAction action : actions) {
            int suivantX = teteX;
            int suivantY = teteY;

            //prochaine position
            if(!mur_dans_la_carte(snakeGame)){
                switch (action) {
                    case MOVE_UP:
                        suivantY -= 1;
                        if (suivantY < 0) suivantY = snakeGame.carte.getSizeY() - 1;
                        break;
                    case MOVE_DOWN:
                        suivantY += 1;
                        if (suivantY >= snakeGame.carte.getSizeY()) suivantY = 0;
                        break;
                    case MOVE_LEFT:
                        suivantX -= 1;
                        if (suivantX < 0) suivantX = snakeGame.carte.getSizeX() - 1;
                        break;
                    case MOVE_RIGHT:
                        suivantX += 1;
                        if (suivantX >= snakeGame.carte.getSizeX()) suivantX = 0;
                        break;
                }
            }
            else {
                switch (action) {
                    case MOVE_UP:
                        suivantY -= 1;
                        break;
                    case MOVE_DOWN:
                        suivantY += 1;
                        break;
                    case MOVE_LEFT:
                        suivantX -= 1;
                        break;
                    case MOVE_RIGHT:
                        suivantX += 1;
                        break;
                }
            }

            if(!isLegalMove(action)) continue;

            if (snakeGame.carte.get_walls()[suivantX][suivantY]) {
                continue;
            }

            //collision avec autre snake plus long
            boolean autre_snake_Collision = false;
            for(Snake s : snakeGame.snake_liste){
                if((s.taille >= snake.taille)&&(s.couleur != snake.couleur)&&(!s.is_invincible)){
                    for (int i = 0; i < s.position.size()-1; i++) {//commence à 1 pour ignorer la tête
                        Position bodyPart = s.position.get(i);
                        if (bodyPart.getX() == suivantX && bodyPart.getY() == suivantY) {
                            autre_snake_Collision = true;
                            break;
                        }
                    }
                }
            }
            if (autre_snake_Collision) {
                continue;
            }



            //collision avec le corps
            boolean isBodyCollision = false;
            for (int i = 1; i < snake.position.size(); i++) {//commence à 1 pour ignorer la tête
                Position bodyPart = snake.position.get(i);
                if (bodyPart.getX() == suivantX && bodyPart.getY() == suivantY) {
                    isBodyCollision = true;
                    break;
                }
            }
            if (isBodyCollision) {
                continue;
            }

            int distance = snakeGame.carte.getSizeX()*snakeGame.carte.getSizeY();
            if(mur_dans_la_carte(snakeGame))
                distance = calculateDistance_avec_mur(suivantX, suivantY, pommeX, pommeY, snakeGame);
            else
                distance = calculateDistance_sans_mur(suivantX, suivantY, pommeX, pommeY, snakeGame);

            if ((distance <= minDistance) && 
                (!snake.is_sick || (distance > snake.tour_malade_restant || minDistance == snakeGame.carte.getSizeX()*snakeGame.carte.getSizeY()))) {
                boolean prochain_boule_jaune = false;
                for(Item item : snakeGame.item_liste){
                    if(item.itemType == ItemType.SICK_BALL){
                        if((suivantX == item.x)&&(suivantY == item.y)){
                            prochain_boule_jaune = true;
                        }
                    }
                }

                if(((!snake.is_sick)&&(20 > distance)&& prochain_boule_jaune)&&( minDistance != snakeGame.carte.getSizeX()*snakeGame.carte.getSizeY())){
                    //System.out.println("boule jaune ne pas aller");
                }
                else{
                    minDistance = distance;
                    meilleure_action = action;
                }
            }


        }


        snake.agentAction = meilleure_action;
        return meilleure_action;
    }


    public int calculateDistance_sans_mur(int x1, int y1, int x2, int y2,SnakeGame snakeGame) {
        int dx = Math.min(Math.abs(x2 - x1), snakeGame.carte.getSizeX() - Math.abs(x2 - x1));
        int dy = Math.min(Math.abs(y2 - y1), snakeGame.carte.getSizeY() - Math.abs(y2 - y1));
        return dx + dy;
    }

    public int calculateDistance_avec_mur(int x1, int y1, int x2, int y2,SnakeGame snakeGame) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    public boolean mur_dans_la_carte(SnakeGame snakeGame){
        boolean[][] walls = snakeGame.carte.get_walls();
    
        if (walls == null) {
            return false;
        }

        for (int i = 0; i < walls.length; i++) {
            for (int j = 0; j < walls[i].length; j++) {
                if (walls[i][j]) {
                    return true;
                }
            }
        }

        return false;
    }
    
}