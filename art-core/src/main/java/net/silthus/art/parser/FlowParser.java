package net.silthus.art.parser;

import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTParseException;
import net.silthus.art.api.parser.ARTParser;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.trigger.TriggerContext;
import org.apache.commons.lang3.NotImplementedException;

import javax.inject.Inject;
import java.util.List;

public class FlowParser implements ARTParser {

    private final ActionManager actionManager;

    @Inject
    public FlowParser(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public boolean matches(ARTConfig config) {
        return config.getArt() != null
                && !config.getArt().isEmpty()
                && config.getArt().parallelStream().allMatch(o -> o instanceof String);
    }

    @Override
    public List<ActionContext<?, ?>> parseActions(ARTConfig config) throws ARTParseException {
        throw new NotImplementedException();
    }

    @Override
    public List<RequirementContext<?, ?>> parseRequirements(ARTConfig config) throws ARTParseException {
        throw new NotImplementedException();
    }

    @Override
    public List<TriggerContext<?, ?>> parseTrigger(ARTConfig config) throws ARTParseException {
        throw new NotImplementedException();
    }
}
