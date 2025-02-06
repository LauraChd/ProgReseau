package snake.etat;

import snake.view.ViewCommand;

public class EtatInitial implements Etat {

    ViewCommand viewCommand;

    public EtatInitial(ViewCommand viewCommand){
        this.viewCommand=viewCommand;
    }

    public void restart() {
        throw new UnsupportedOperationException("déjà état initial");
    }

    public void step() {
        viewCommand.abstractController.step();
        viewCommand.setEtat(new EtatPause(viewCommand));
        viewCommand.updateButtons(true,true,true,false);
    }

    public void play() {
        this.viewCommand.abstractController.play();
        this.viewCommand.setEtat(new EtatLecture(this.viewCommand));
        this.viewCommand.updateButtons(true,false,false,true);
    }

    public void pause() {
        throw new UnsupportedOperationException("mode état initial donc déja en pause");
    }

    public void setSpeed(double speed) {
        throw new UnsupportedOperationException("mode état initial");
    }
    
}
