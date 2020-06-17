package net.silthus.art.parser.flow.types;

import lombok.Getter;
import net.silthus.art.api.ARTType;
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ARTObjectConfig;
import net.silthus.art.api.parser.ARTParseException;
import net.silthus.art.api.parser.flow.ARTTypeParser;
import net.silthus.art.parser.flow.Constants;

import javax.inject.Inject;
import java.util.Optional;

public class ActionTypeParser extends ARTTypeParser<ActionContext<?, ?>> {

    @Getter
    private final ActionManager actionManager;

    @Inject
    public ActionTypeParser(ActionManager actionManager) {
        super(Constants.ART_TYPE_MATCHER_CHARS.get(ARTType.ACTION));
        this.actionManager = actionManager;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActionContext<?, ?> parse() throws ARTParseException {

        String identifier = getMatcher().group("identifier");
        Optional<ActionFactory<?, ?>> optionalAction = getActionManager().getFactory(identifier);

        if (optionalAction.isEmpty()) {
            throw new ARTParseException("No action with identifier \"" + identifier + "\" found!");
        }

        ActionFactory<?, ?> actionFactory = optionalAction.get();
        ARTObjectConfig actionConfig = parseARTConfig(actionFactory, new ActionConfig<>(), ActionConfig.CONFIG_FIELD_INFORMATION);

        return (ActionContext<?, ?>) actionFactory.create(actionConfig);
    }
}
