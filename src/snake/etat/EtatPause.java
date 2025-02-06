package snake.etat;

import snake.view.ViewCommand;


public class EtatPause implements Etat {

    ViewCommand viewCommand;

    public EtatPause(ViewCommand viewCommand){
        this.viewCommand=viewCommand;
    }

    public void restart() {
        viewCommand.abstractController.restart();
        viewCommand.setEtat(new EtatInitial(viewCommand));
        viewCommand.updateButtons(false, true, true, false);
    }

    public void step() {
        viewCommand.abstractController.step();
        viewCommand.updateButtons(true, true, true, false);
    }

    public void play() {
        viewCommand.abstractController.play();
        viewCommand.setEtat(new EtatLecture(viewCommand));
        viewCommand.updateButtons(true, false, false, true);
    }

    public void pause() {
        throw new UnsupportedOperationException("jeu déjà en pause");
    }

    public void setSpeed(double speed) {
        viewCommand.abstractController.setSpeed(speed);
    }
}
