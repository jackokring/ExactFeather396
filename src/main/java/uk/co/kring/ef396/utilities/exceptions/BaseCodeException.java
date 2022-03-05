package uk.co.kring.ef396.utilities.exceptions;

import net.minecraft.world.entity.Entity;
import uk.co.kring.ef396.ExactFeather;

import java.util.HashMap;

public abstract class BaseCodeException extends Exception  {

    private static HashMap<Thread, Entity> actionEntity = new HashMap<>();//TODO ??

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
        Throwable f = e.getCause();
        if(f instanceof BaseCodeException) {
            ExactFeather.LOGGER.info(e.getMessage());//log Runtime throwing
            return (BaseCodeException) f;
        } else {
            throw e;//continue with throw
        }
    }

    protected abstract void actionTry() throws BaseCodeException;
    protected abstract void actionCatch(BaseCodeException exception)
            throws BaseCodeException;
}
