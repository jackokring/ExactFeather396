package uk.co.kring.ef396.utilities.exceptions;

import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entities.goals.AICommon;

import java.util.HashMap;

public abstract class BaseCodeException extends Exception  {

    private static HashMap<Thread, AICommon> actionEntity = new HashMap<>();//TODO ??

    public final void emote() {
        AICommon c = actionEntity.get(Thread.currentThread());
        try {
            actionTry(c);
        } catch(BaseCodeException e) {
            try {
                actionCatch(e, c);
            } catch(Exception f) {
                //null as not relevant to handle.
            }
        } catch(RuntimeException e) {
            BaseCodeException f = catchAssist(e);
            try {
                f.actionCatch(f, c);
            } catch(Exception g) {
                //null as not relevant to handle
            }
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
