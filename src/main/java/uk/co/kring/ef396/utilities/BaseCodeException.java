package uk.co.kring.ef396.utilities;

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

    public static final void throwAssist(BaseCodeException e) {
        throw new RuntimeException(e);
    }

    protected abstract void actionTry() throws BaseCodeException;
    protected abstract void actionCatch(BaseCodeException exception)
            throws BaseCodeException;
}
