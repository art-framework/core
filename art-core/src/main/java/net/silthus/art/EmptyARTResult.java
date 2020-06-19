package net.silthus.art;

import net.silthus.art.api.parser.ARTResult;

class EmptyARTResult implements ARTResult {

    @Override
    public boolean test(Object target) {
        return false;
    }

    @Override
    public void execute(Object target) {

    }
}
