package snake.controller;
import snake.game.Game;


public abstract class AbstractController {
    private Game game;

    public AbstractController (Game game){
        this.game = game;
    }

    public void restart(){
        game.init();
    }

    public void step(){
        game.step();
    }

    public void play(){
        game.launch();
    }

    public void pause(){
        game.pause();
    }
    @SuppressWarnings("removal")
    public void setSpeed(double speed){
        long l = (new Double(speed)).longValue();
        game.time = 1000/l;
    }


}
