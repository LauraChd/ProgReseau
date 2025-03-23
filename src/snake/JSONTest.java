package snake;

import java.util.ArrayList;
import java.util.Arrays;

import snake.game.GameState;
import snake.game.Item;
import snake.game.Snake;
import snake.utils.AgentAction;
import snake.utils.ColorSnake;
import snake.utils.FeaturesItem;
import snake.utils.ItemType;
import snake.utils.Position;

public class JSONTest {

    public static void main(String[] args) {
     try {
            // Création des positions des serpents
            Snake snake1 = new Snake(new ArrayList<>(Arrays.asList(
                new Position(1, 1),
                new Position(1, 2)
            )), ColorSnake.Green);

            snake1.setScore(5);
            snake1.setAgentAction(AgentAction.MOVE_RIGHT);
            snake1.setIs_sick(true);
            snake1.setIs_sick(false);

            Snake snake2 = new Snake(new ArrayList<>(Arrays.asList(
                new Position(5, 5),
                new Position(5, 6)
            )), ColorSnake.Red);

            snake2.setScore(8);
            snake2.setAgentAction(AgentAction.MOVE_LEFT);
            snake2.setIs_sick(true);
            snake2.setIs_invincible(true);

            // Création des items
            Item item1 = new Item(3, 3, ItemType.INVINCIBILITY_BALL);
            Item item2 = new Item(7, 2, ItemType.APPLE);
            
            // Création de l'état du jeu
            GameState gameState = new GameState(snake1, snake2, Arrays.asList(item1, item2));

            // Sérialisation en JSON
            String json = gameState.toJson();
            System.out.println("JSON:\n" + json);

            // Désérialisation du JSON
            GameState newGameState = GameState.fromJson(json);
            System.out.println("\nSnake1 Score: " + newGameState.getSnake1().getScore());
            System.out.println("Snake2 State: " + newGameState.getSnake2().isIs_sick());
            System.out.println("Items: " + newGameState.getItems());

        } catch (Exception e) {
            e.printStackTrace();
        }
}
}

