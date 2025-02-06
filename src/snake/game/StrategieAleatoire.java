package snake.game;

import snake.utils.AgentAction;

public class StrategieAleatoire implements Strategie {

    Snake snake;

    public StrategieAleatoire(Snake snake){
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
        AgentAction[] direction = {AgentAction.MOVE_UP, AgentAction.MOVE_DOWN, AgentAction.MOVE_LEFT,AgentAction.MOVE_RIGHT,};
        AgentAction choix = direction[(int) (Math.random() * direction.length)];
        return choix;
    }

    
    
}
