package net.silthus.art.parser.flow;

import lombok.Data;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtResult;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.requirements.RequirementContext;

import java.util.List;

@Data
public class FlowParserResult implements ArtResult {

    private final List<ArtContext<?, ?>> art;

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> boolean test(TTarget target) {

        return getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof RequirementContext)
                .map(artContext -> (RequirementContext<TTarget, ?>) artContext)
                .allMatch(tTargetRequirement -> tTargetRequirement.test(target));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> void execute(TTarget target) {

        getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof ActionContext)
                .map(artContext -> (ActionContext<TTarget, ?>) artContext)
                .forEach(tTargetRequirement -> tTargetRequirement.execute(target));
    }
}
