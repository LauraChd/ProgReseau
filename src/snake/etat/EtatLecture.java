package snake.etat;

import snake.view.ViewCommand;


public class EtatLecture implements Etat {

    ViewCommand viewCommand;

    public EtatLecture(ViewCommand viewCommand){
        this.viewCommand=viewCommand;
    }

    public void restart() {
        viewCommand.abstractController.restart();
        viewCommand.setEtat(new EtatInitial(viewCommand));
        viewCommand.updateButtons(false,true,true,false);
    }

    public void step() {
        throw new UnsupportedOperationException("mode lecture");
    }

    public void play() {
        throw new UnsupportedOperationException("déja en lecture");
    }

    public void pause() {
        viewCommand.abstractController.pause();
        viewCommand.setEtat(new EtatPause(viewCommand));
        viewCommand.updateButtons(true,true,true,false);
        
    }

    public void setSpeed(double speed) {
        viewCommand.abstractController.setSpeed(speed);
    }
}
