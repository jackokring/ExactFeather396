package uk.co.kring.ef396.utilities.exceptions;

import uk.co.kring.ef396.entities.goals.AICommon;

public class StubbornException extends BaseCodeException {

    @Override
    protected void actionTry(AICommon ai) throws BaseCodeException {
        // Do nothing
    }

    @Override
    protected void actionCatch(BaseCodeException exception, AICommon ai) throws BaseCodeException {
        // Never done as do nothing never causes an exception
    }
}
