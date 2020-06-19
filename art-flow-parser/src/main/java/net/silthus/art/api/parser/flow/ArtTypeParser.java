package net.silthus.art.api.parser.flow;

import com.google.common.base.Strings;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtFactory;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.parser.flow.types.ConfigParser;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class ArtTypeParser<TContext extends ArtContext<?, ?>> extends Parser<TContext> {

    public ArtTypeParser(char typeMatcher) {
        // regexr.com/56s09
        super(Pattern.compile("^" + typeMatcher + "(?<identifier>[\\w\\-.\\d:]+)(\\[(?<config>[\\w\\d:., ]*\\]))? ?(?<userConfig>[\\w\\d:., ]+)?$"));
    }

    public String getIdentifier() {
        return getMatcher().group("identifier");
    }

    public Optional<String> getConfig() {
        String config = getMatcher().group("config");
        if (Strings.isNullOrEmpty(config)) return Optional.empty();
        return Optional.of(config);
    }

    public String getUserConfig() {
        return getMatcher().group("userConfig");
    }

    protected <TConfig extends ArtObjectConfig<?>> TConfig parseARTConfig(ArtFactory<?, ?, ?> factory, TConfig artConfig, Map<String, ConfigFieldInformation> configFieldInformationMap) throws ArtParseException {

        ConfigParser<TConfig> actionConfigParser = new ConfigParser<>(artConfig, configFieldInformationMap);
        Optional<String> config = getConfig();
        if (config.isPresent() && actionConfigParser.accept(config.get())) {
            artConfig = actionConfigParser.parse();
        }

        if (factory.getConfigClass().isPresent()) {
            try {
                ConfigParser<?> configParser = new ConfigParser<>(factory.getConfigClass().get().getConstructor().newInstance(), factory.getConfigInformation());
                String userConfig = getUserConfig();
                if (configParser.accept(userConfig)) {
                    artConfig.setWith(configParser.parse());
                }

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ArtParseException("Unable to parse config of " + factory.getIdentifier(), e);
            }
        }

        return artConfig;
    }
}
