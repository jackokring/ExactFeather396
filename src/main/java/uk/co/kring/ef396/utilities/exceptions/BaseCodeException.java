package uk.co.kring.ef396.utilities.exceptions;

import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entities.goals.AICommon;

import java.util.HashMap;

public abstract class BaseCodeException extends Exception  {

    private static HashMap<Thread, AICommon> actionEntity = new HashMap<>();

    public final void emote() {
        AICommon c = actionEntity.get(Thread.currentThread());
        synchronized(c) {
            try {
                actionTry(c);
            } catch (BaseCodeException e) {
                try {
                    actionCatch(e, c);
                } catch (Exception f) {
                    //null as not relevant to handle.
                }
            } catch (RuntimeException e) {
                BaseCodeException f = catchAssist(e);
                try {
                    f.actionCatch(f, c);
                } catch (Exception g) {
                    //null as not relevant to handle
                }
            }
        }
    }

    public static final Thread threadAssist(AICommon ai) {
        Thread t = new Thread();
        actionEntity.put(t, ai);
        return t;//TODO
    }

    public static final void joinAssist() {
        actionEntity.remove(Thread.currentThread());
        try {
            Thread.currentThread().join();
        } catch(InterruptedException e) {
            //TODO
        }
    }

    public static final void throwAssist(String why, BaseCodeException e) {
        throw new RuntimeException(why, e);
    }

    public static final BaseCodeException catchAssist(RuntimeException e) {
        Throwable f = e.getCause();
        if(f instanceof BaseCodeException) {
            ExactFeather.LOGGER.info(e.getMessage());//log Runtime throwing
            return (BaseCodeException) f;
        } else {
            throw e;//continue with throw
        }
    }

    protected abstract void actionTry(AICommon ai) throws BaseCodeException;
    protected abstract void actionCatch(BaseCodeException exception, AICommon ai)
            throws BaseCodeException;
}
