package uk.co.kring.ef396.entities.goals;

import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.entities.goals.exceptions.BaseCodeException;

import java.util.function.Consumer;

public class GoalThread extends Thread {

    private AICommon ai;

    public GoalThread(AICommon ai) {
        ai = ai;
        this.start();
    }

    public final AICommon getAI() {
        return ai;
    }

    public BaseCodeException performSyncAction(Consumer<HogEntity> use) {
        return ai.performSyncAction(use);// lock entity
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
