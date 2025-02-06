package snake.game;
public class SimpleGame extends Game{

    public SimpleGame (int maxturn){
        super(maxturn);
    }

    public void gameOver(){
        System.out.println("Jeu perdu");
    }

    public void initializeGame(){
        System.out.println("Initialisation du jeu");
    }

    public boolean gameContinue(){
        return true;
    }

    public void takeTurn(){
        System.out.println("Tour "+ turn +" du jeu en cours");
    }
    


}
