package net.silthus.art.testing;

import net.silthus.art.api.trigger.AbstractTarget;

public class StringTarget extends AbstractTarget<String> {

    public StringTarget(String source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        return getSource();
    }
}
