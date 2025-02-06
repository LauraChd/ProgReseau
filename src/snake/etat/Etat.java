package snake.etat;
public interface Etat {
    public void restart();
    public void step();
    public void play();
    public void pause();
    public void setSpeed(double speed);
}
