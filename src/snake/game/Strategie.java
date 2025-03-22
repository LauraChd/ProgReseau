package snake.game;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import snake.utils.AgentAction;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface Strategie {
    public boolean isLegalMove(AgentAction agentAction);
    public AgentAction direction_choisie(SnakeGame snakeGame);
}
