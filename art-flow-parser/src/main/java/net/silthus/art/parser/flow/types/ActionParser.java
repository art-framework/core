package net.silthus.art.parser.flow.types;

import lombok.Getter;
import net.silthus.art.api.ActionContext;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.flow.ArtTypeParser;
import net.silthus.art.parser.flow.Constants;

import javax.inject.Inject;
import java.util.Optional;

public class ActionParser extends ArtTypeParser<ActionContext<?, ?>, ActionConfig<?>> {

    @Getter
    private final ActionManager actionManager;

    @Inject
    public ActionParser(ActionManager actionManager) {
        super(Constants.ART_TYPE_MATCHER_CHARS.get(Action.class));
        this.actionManager = actionManager;
    }

    @Override
    protected ActionConfig<?> createConfig(Object config) {
        return new ActionConfig<>(config);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActionContext<?, ?> parse() throws ArtParseException {

        String identifier = getMatcher().group("identifier");
        Optional<ActionFactory<?, ?>> optionalAction = getActionManager().getFactory(identifier);

        if (optionalAction.isEmpty()) {
            throw new ArtParseException("No action with identifier \"" + identifier + "\" found");
        }

        ActionFactory<?, ?> actionFactory = optionalAction.get();
        ActionConfig actionConfig = parseARTConfig(actionFactory, ActionConfig.CONFIG_FIELD_INFORMATION);

        return actionFactory.create(actionConfig);
    }
}
