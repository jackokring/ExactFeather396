package uk.co.kring.ef396.entities.goals;

public class GoalThread extends Thread {

    private AICommon ai;

    public GoalThread(AICommon ai) {
        ai = ai;
        this.start();
    }

    public final AICommon getAI() {
        return ai;
    }

    @Override
    public final void run() {
        //pre
        action();
        //post
    }

    public void action() {

    }
}
