package snake.controller;
import snake.game.SnakeGame;
import snake.view.ViewSimpleGame;

public class ControllerSimpleGame extends AbstractController{

    public ControllerSimpleGame(SnakeGame snakeGame) {
        super(snakeGame);
        @SuppressWarnings("unused")
        ViewSimpleGame v1 = new ViewSimpleGame(snakeGame);
    }
}