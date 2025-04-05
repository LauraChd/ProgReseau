package snake.game;

import snake.utils.AgentAction;

public class StrategieClavier implements Strategie {

    private AgentAction currentAction = AgentAction.MOVE_DOWN;
    //Snake snake;
    
    public StrategieClavier(){}
    
    @Override
    public AgentAction getCurrentAction() {
        return currentAction;
    }
    
    @Override
    public void setCurrentAction(AgentAction currentAction) {
        this.currentAction = currentAction;
    }


    

    

    


    
    
}

