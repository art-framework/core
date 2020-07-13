package net.silthus.art.test.targets;

import net.silthus.art.AbstractTarget;

public class StringTarget extends AbstractTarget<String> {

    public StringTarget(String source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        return getSource();
    }
}
