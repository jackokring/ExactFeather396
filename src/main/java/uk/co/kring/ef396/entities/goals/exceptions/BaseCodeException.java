package uk.co.kring.ef396.entities.goals.exceptions;

import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entities.goals.AICommon;
import uk.co.kring.ef396.entities.goals.GoalThread;

public abstract class BaseCodeException extends Exception  {

    public final void emote() {
        AICommon c;
        try {
            c = ((GoalThread)Thread.currentThread()).getAI();
        } catch(Exception e) {
            throw new UnsupportedOperationException(Thread.currentThread().toString()
                    + " must be a GoalThread");
        }
        synchronized(c) {
            try {
                actionTry(c);
            } catch (BaseCodeException e) {
                try {
                    e.emote();
                } catch (Exception f) {
                    explain(e, c);
                }
            } catch (RuntimeException e) {
                BaseCodeException f = catchAssist(e);
                try {
                    f.emote();
                } catch (Exception g) {
                    explain(e, c);
                }
            }
        }
    }

    private void explain(Exception e, AICommon c) {
        //null as not relevant to handle
        ExactFeather.LOGGER.info(e.getClass().toString()
                + " requests synchronized throw nest in "
                + Thread.currentThread().toString()
                + " doing " + c.toString() + " which is locked and needs release");
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
}
