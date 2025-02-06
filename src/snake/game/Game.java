package snake.game;
@SuppressWarnings("deprecation")
public abstract class Game extends java.util.Observable implements Runnable{ //le modèle => observable
    public int turn;
    int maxturn;
    boolean isRunning;//jeu mis en pause ou non
    Thread thread;
    public long time;

    public Game (int maxturn){
        this.turn = 0;
        this.maxturn = maxturn;
        this.time = 1000;
    }

    public void init() {
        this.turn = 0;
        this.isRunning = false;//si on ne veut pas que ça fasse play depuis le début
        this.time = 1000;
        initializeGame();
        this.setChanged();
        notifyObservers();
    }

    public void step(){
        if(gameContinue()&&(turn<maxturn)){
            this.turn++;
            takeTurn();
        }
            
        else {
            this.isRunning = false;
            gameOver();
        }
        this.setChanged();
        notifyObservers();  
    }

    public abstract void gameOver();

    public abstract void initializeGame();

    public abstract boolean gameContinue();

    public abstract void takeTurn();

    public void pause(){
        this.isRunning = false;
    }

    public void run(){
        while(this.isRunning){
            step();
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void launch(){
        this.isRunning = true;
        thread = new Thread(this);
        thread.start();//lance automatiquement la méthode run
    }
}
