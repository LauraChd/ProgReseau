package snake.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import snake.utils.AgentAction;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")  // Utiliser "type" pour simplifier la sérialisation
@JsonSubTypes({
    @JsonSubTypes.Type(value = StrategieClavier.class, name = "StrategieClavier"),
    @JsonSubTypes.Type(value = StrategieAleatoire.class, name = "StrategieAleatoire"),
    @JsonSubTypes.Type(value = StrategieIA.class, name = "StrategieIA")
})

public interface Strategie {
    //public boolean isLegalMove(AgentAction agentAction);
    //public AgentAction direction_choisie(SnakeGame snakeGame);
    /*public Snake getSnake();
    public void setSnake(Snake snake);*/
    public AgentAction getCurrentAction(Snake snake, SnakeGame game);
    
    public void setCurrentAction(AgentAction currentAction);
}
