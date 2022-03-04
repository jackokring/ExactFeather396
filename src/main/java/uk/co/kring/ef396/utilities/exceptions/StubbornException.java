package uk.co.kring.ef396.utilities.exceptions;

public class StubbornException extends BaseCodeException {

    @Override
    protected void actionTry() throws BaseCodeException {
        // Do nothing
    }

    @Override
    protected void actionCatch(BaseCodeException exception) throws BaseCodeException {
        // Never done as do nothing never causes an exception
    }
}
