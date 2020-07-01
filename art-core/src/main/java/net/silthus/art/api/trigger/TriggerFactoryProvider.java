package net.silthus.art.api.trigger;

import net.silthus.art.api.Trigger;

public interface TriggerFactoryProvider {

    <TConfig> TriggerFactory<TConfig> create(Trigger trigger);
}
