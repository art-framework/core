package io.artframework.bukkit.parser;

import io.artframework.ActionFactory;
import io.artframework.ConfigMap;
import io.artframework.Scope;
import io.artframework.bukkit.actions.ConsoleCommandAction;
import io.artframework.conf.ActionConfig;
import io.artframework.parser.flow.ArtObjectContextLineParser;
import io.artframework.parser.flow.FlowType;

import java.util.Iterator;
import java.util.Optional;

public class CommandLineParser extends ArtObjectContextLineParser<ActionFactory<?>> {

    public CommandLineParser(Iterator<String> iterator, Scope scope) {

        super(scope, iterator, new FlowType("command", "\\/"));
    }

    @Override
    protected String userConfig() {

        return "\"" + getIdentifier() + " " + super.userConfig() + "\"";
    }

    @Override
    protected Optional<ActionFactory<?>> factory(String identifier) {

        return scope().configuration().actions().get(ConsoleCommandAction.IDENTIFIER);
    }

    @Override
    protected ConfigMap configMap() {

        return ActionConfig.configMap();
    }
}
