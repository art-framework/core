package net.silthus.art.api.parser;

import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.List;

public interface ARTParser {

    boolean matches(ARTConfig config);

    List<ActionContext<?, ?>> parseActions(ARTConfig config) throws ARTParseException;

    List<RequirementContext<?, ?>> parseRequirements(ARTConfig config) throws ARTParseException;

    List<TriggerContext<?, ?>> parseTrigger(ARTConfig config) throws ARTParseException;

}
