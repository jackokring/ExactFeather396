package uk.co.kring.ef396.utilities.exceptions;

import uk.co.kring.ef396.ExactFeather;

public abstract class BaseCodeException extends Exception  {

    public final void emote() {
        try {
            actionTry();
        } catch(BaseCodeException e) {
            try {
                actionCatch(e);
            } catch(BaseCodeException f) {
                //null as not relevant to handle.
            }
        }
    }

    public static final void throwAssist(String why, BaseCodeException e) {
        throw new RuntimeException(why, e);
    }

    public static final BaseCodeException catchAssist(String because, RuntimeException e) {
        String why = e.getMessage();
        BaseCodeException f = null;
        try {
             f = (BaseCodeException) e.getCause();
        } catch(ClassCastException g) {
            ExactFeather.LOGGER.info("Not a BaseCodeException, " + because);
        }
        ExactFeather.LOGGER.info(why + ", " + because);//log
        return f;
    }

    protected abstract void actionTry() throws BaseCodeException;
    protected abstract void actionCatch(BaseCodeException exception)
            throws BaseCodeException;
}
