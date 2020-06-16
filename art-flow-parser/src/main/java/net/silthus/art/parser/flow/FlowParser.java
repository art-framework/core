package net.silthus.art.parser.flow;

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
import java.util.Objects;
import java.util.stream.Collectors;

public class FlowParser implements ARTParser {

    private final ActionManager actionManager;

    @Inject
    public FlowParser(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public boolean matches(ARTConfig config) {
        return Objects.nonNull(config)
                && Objects.nonNull(config.getArt())
                && !config.getArt().isEmpty()
                && config.getArt().parallelStream().allMatch(o -> o instanceof String);
    }

    private List<String> extract(ARTConfig config) {
        return config.getArt().stream()
                .filter(object -> object instanceof String)
                .map(object -> (String) object)
                .collect(Collectors.toList());
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
