package snake.game;

import snake.utils.AgentAction;

public interface Strategie {
    public boolean isLegalMove(AgentAction agentAction);
    public AgentAction direction_choisie(SnakeGame snakeGame);
}
