package net.silthus.art.parser.flow;

import net.silthus.art.api.ARTObject;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.parser.ARTParseException;
import net.silthus.art.api.parser.ARTParser;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;

import javax.inject.Inject;

public class FlowParser implements ARTParser {

    private final ActionManager actionManager;

    @Inject
    public FlowParser(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public boolean matches(Object configObject) {
        return configObject instanceof String;
    }

    private String toConfig(Object configObject) {
        return (String) configObject;
    }

    @Override
    public ARTObject next(Object configObject) throws ARTParseException {
        Validate.notNull(configObject);
        if (!matches(configObject))
            throw new ARTParseException("Config of type " + configObject.getClass().getCanonicalName() + " cannot be parsed by " + getClass().getCanonicalName() + ". Make sure check matches() before passing the config.");

        String config = toConfig(configObject);

        throw new NotImplementedException();
    }
}
