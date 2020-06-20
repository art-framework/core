package net.silthus.art.api.requirements;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.config.ArtObjectConfig;

/**
 * The {@link RequirementConfig} holds general information about the execution
 * properties of the requirement.
 * e.g. if the {@link Requirement} should be negated
 *
 * @param <TConfig> custom config type of the requirement
 */
@Getter
@ConfigurationElement
@EqualsAndHashCode(callSuper = true)
public class RequirementConfig<TConfig> extends ArtObjectConfig<TConfig> {

    private final boolean persistent = false;
    private final int order = 0;
    private final int requiredCount = 0;

    public RequirementConfig() {
    }

    public RequirementConfig(TConfig with) {
        super(with);
    }
}
