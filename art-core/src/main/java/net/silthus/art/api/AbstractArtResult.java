package net.silthus.art.api;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.requirements.RequirementContext;

import java.util.List;

@Getter(AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class AbstractArtResult implements ArtResult {

    private final ArtConfig config;
    private final List<ArtContext<?, ?>> art;

    public AbstractArtResult(ArtConfig config, List<ArtContext<?, ?>> art) {
        this.config = config;
        this.art = ImmutableList.copyOf(art);
    }

    protected abstract <TTarget> boolean filter(TTarget target, ArtContext<TTarget, ?> context);

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> boolean test(TTarget target) {

        return getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof RequirementContext)
                .map(artContext -> (RequirementContext<TTarget, ?>) artContext)
                .filter(requirement -> filter(target, requirement))
                .allMatch(tTargetRequirement -> tTargetRequirement.test(target));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> void execute(TTarget target) {

        getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof ActionContext)
                .map(artContext -> (ActionContext<TTarget, ?>) artContext)
                .filter(action -> filter(target, action))
                .forEach(action -> action.execute(target));
    }
}
