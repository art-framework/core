package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.api.config.ArtConfigException;
import net.silthus.art.conf.ConfigFieldInformation;
import net.silthus.art.conf.DefaultConfigMap;
import net.silthus.art.conf.KeyValuePair;

import java.util.List;
import java.util.Map;

public interface ConfigMap {

    static ConfigMap of(ConfigMapType type, Map<String, ConfigFieldInformation> configFields) {
        return new DefaultConfigMap(type, configFields);
    }

    ConfigMapType getType();

    <TConfig> TConfig applyTo(@NonNull TConfig config);

    ConfigMap loadValues(@NonNull List<KeyValuePair> keyValuePairs) throws ArtConfigException;

    Map<String, ConfigFieldInformation> getConfigFields();

    boolean isLoaded();
}
