package uk.co.kring.ef396.utilities.exceptions;

import uk.co.kring.ef396.ExactFeather;

public abstract class BaseCodeException extends Exception  {

    public final void emote() {
        try {
            actionTry();
        } catch(BaseCodeException e) {
            try {
                actionCatch(e);
            } catch(Exception f) {
                //null as not relevant to handle.
            }
        } catch(RuntimeException e) {
            BaseCodeException f = catchAssist(e);
            try {
                f.actionCatch(f);
            } catch(Exception g) {
                //null as not relevant to handle
            }
        }
    }

    public static final void throwAssist(String why, BaseCodeException e) {
        throw new RuntimeException(why, e);
    }

    public static final BaseCodeException catchAssist(RuntimeException e) {
        String why = e.getMessage();
        BaseCodeException f = null;
        try {
             f = (BaseCodeException) e.getCause();
        } catch(ClassCastException g) {
            throw e;//continue with throw
        }
        ExactFeather.LOGGER.info(why);//log Runtime throwing
        return f;
    }

    protected abstract void actionTry() throws BaseCodeException;
    protected abstract void actionCatch(BaseCodeException exception)
            throws BaseCodeException;
}
