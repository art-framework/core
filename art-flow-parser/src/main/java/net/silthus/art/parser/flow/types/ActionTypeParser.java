package net.silthus.art.parser.flow.types;

import lombok.Getter;
import net.silthus.art.api.ARTType;
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.parser.ARTParseException;
import net.silthus.art.api.parser.flow.ARTTypeParser;
import net.silthus.art.api.parser.flow.ConfiguredARTType;

import javax.inject.Inject;
import java.util.Optional;

public class ActionTypeParser extends ARTTypeParser<ConfiguredARTType<ActionConfig<?>>> {

    @Getter
    private final ActionManager actionManager;

    @Inject
    public ActionTypeParser(ActionManager actionManager) {
        super('!');
        this.actionManager = actionManager;
    }

    @Override
    public ConfiguredARTType<ActionConfig<?>> parse() throws ARTParseException {



        String identifier = getMatcher().group("name");

        if (!getActionManager().exists(identifier)) {
            throw new ARTParseException("No action with identifier \"" + identifier + "\" found!");
        }

        ActionFactory<?, ?> actionFactory = getActionManager().getFactory(identifier).get();

        return new ConfiguredARTType<>(ARTType.ACTION, "foobar", new ActionConfig<>());

//
//        FlowConfiguration configuration = new FlowConfiguration();
//        FlowType flowType = optionalFlowType.get();
//
//        if (!getConfigParser().filter(parser -> parser.hasAlias(FlowType.ACTION, identifier)).isPresent() && !ActionAPI.isAction(identifier)) {
//            ActionAPI.UNKNOWN_ACTIONS.add(identifier);
//            throw new FlowException(flowType.name() + " " + identifier + " inside flow not found!");
//        }
//        information = ActionAPI.getActionInformation(identifier);
//        ConfigParser configParser;
//
//        if (getConfigParser().filter(parser -> parser.hasAlias(flowType, identifier)).isPresent()) {
//            configParser = new ConfigParser();
//        } else {
//            configParser = information.map(ConfigParser::new).orElseGet(ConfigParser::new);
//        }
//
//        if (configParser.accept(getMatcher().group("config"))) {
//            // if the parser does not match the config is empty
//            configuration = configParser.parse();
//        }
//
//        configuration.set("type", identifier);
//        ActionAPIType actionAPIType = new ActionAPIType(flowType, configuration, identifier);
//        actionAPIType.setCheckingRequirement(checkingRequirement);
//        actionAPIType.setNegate(requirementFailure);
//        return actionAPIType;
    }
}
