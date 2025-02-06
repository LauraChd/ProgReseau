package snake.game;

import java.util.ArrayList;

import snake.utils.ColorSnake;
import snake.utils.Position;

public class SnakeFactory {
    public Snake creerAgentAleatoire(int x, int y,ColorSnake couleur){

        ArrayList<Position> position = new ArrayList<>();
        position.add(new Position(x, y));

        Snake snake = new Snake(position, couleur);
        snake.strategie = new StrategieAleatoire(snake);
        return snake;
    }
    public Snake creerAgentHumain(int x, int y,ColorSnake couleur){
        
        ArrayList<Position> position = new ArrayList<>();
        position.add(new Position(x, y));

        Snake snake = new Snake(position, couleur);
        snake.strategie = new StrategieClavier(snake);
        return snake;
    }
    public Snake creerAgentIA(int x, int y,ColorSnake couleur){
        
        ArrayList<Position> position = new ArrayList<>();
        position.add(new Position(x, y));

        Snake snake = new Snake(position, couleur);
        snake.strategie = new StrategieIA(snake);
        return snake;
    }
}
