package uk.co.kring.ef396.utilities.exceptions;

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

    protected abstract void actionTry() throws BaseCodeException;
    protected abstract void actionCatch(BaseCodeException exception)
            throws BaseCodeException;
}
