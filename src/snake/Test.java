package snake;

import snake.controller.ControllerSimpleGame;
import snake.controller.ControllerSnakeGame;
import snake.game.SnakeGame;
import snake.model.InputMap;

public class Test {
    
    public static void main(String[] args) throws Exception {


        String layoutPath = "src/snake/layouts/alone.lay"; 
        //String layoutPath = "layouts/aloneNoWall.lay"; 
        //String layoutPath = "layouts/arena.lay"; 
        //String layoutPath = "src/snake/layouts/arenaNoWall.lay"; 
        //String layoutPath = "layouts/small.lay"; 
        //String layoutPath = "src/snake/layouts/smallArena.lay"; 
        //String layoutPath = "layouts/smallArenaNoWall.lay"; 
        //String layoutPath = "layouts/smallNoWall.lay";

        SnakeGame snakeGame = new SnakeGame(10000,new InputMap(layoutPath));
        @SuppressWarnings("unused")
        ControllerSnakeGame controllerSnakeGame = new ControllerSnakeGame(snakeGame, layoutPath);
        @SuppressWarnings("unused")
        ControllerSimpleGame c1 = new ControllerSimpleGame(snakeGame);


        
    }

}
/*modèle => jeu
vue => 2 interfaces (mise à jour, au début juste pour les tours)(observateur du jeu)
controleur => changer la vue

*/