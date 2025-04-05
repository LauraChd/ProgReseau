package snake.game;

import snake.utils.AgentAction;

public class StrategieAleatoire implements Strategie {

    private AgentAction currentAction = AgentAction.MOVE_DOWN;
    //Snake snake;
    
    public StrategieAleatoire(){}
    
    @Override
    public AgentAction getCurrentAction(Snake snake, SnakeGame game) {
        return this.currentAction;
    }
    
    @Override
    public void setCurrentAction(AgentAction currentAction) {
        this.currentAction = currentAction;
    }
    //@Override
    public AgentAction direction_choisie(SnakeGame snakeGame) {
        AgentAction[] direction = {AgentAction.MOVE_UP, AgentAction.MOVE_DOWN, AgentAction.MOVE_LEFT,AgentAction.MOVE_RIGHT,};
        AgentAction choix = direction[(int) (Math.random() * direction.length)];
        return choix;
    }

    
    
}
