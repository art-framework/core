package net.silthus.art.api.requirements;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.silthus.art.api.config.ArtObjectConfig;

/**
 * The {@link RequirementConfig} holds general information about the execution
 * properties of the requirement.
 * e.g. if the {@link Requirement} should be negated
 *
 * @param <TConfig> custom config type of the requirement
 */
@Data
@ConfigurationElement
@EqualsAndHashCode(callSuper = true)
public class RequirementConfig<TConfig> extends ArtObjectConfig<TConfig> {

    private boolean persistent = false;
    private int order = 0;
    private int requiredCount = 0;
}
