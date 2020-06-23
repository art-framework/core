/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.art.parser.flow.parser;

import com.google.common.base.Strings;
import lombok.Getter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.flow.Parser;
import net.silthus.art.parser.flow.ArtType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class ArtTypeParser<TContext extends ArtContext<?, ?, ? extends ArtObjectConfig<?>>, TConfig extends ArtObjectConfig<?>> extends Parser<TContext> {

    @Getter
    private final ArtType artType;

    public ArtTypeParser(ArtType artType) {
        // always edit the regexr link and update the link below!
        // the regexr link and the regex should always match
        // regexr.com/56s09
        super(Pattern.compile("^" + artType.getTypeIdentifier() + "(?<identifier>[\\w\\d:._-]+)([\\[\\(](?<config>[^\\]\\)]*?)[\\]\\)])?( (?<userConfig>.+))?$"));
        this.artType = artType;
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

    protected abstract TConfig createConfig(Object config);

    protected abstract <TFactory extends ArtFactory<?, ?, ?, TConfig>> Optional<TFactory> getFactory(String identifier);

    protected abstract Map<String, ConfigFieldInformation> getConfigFieldMap();

    @Override
    @SuppressWarnings({"unchecked"})
    public TContext parse() throws ArtParseException {

        String identifier = getIdentifier();
        Optional<ArtFactory<?, ?, ?, TConfig>> factoryOptional = getFactory(identifier);

        if (factoryOptional.isEmpty()) {
            throw new ArtParseException("No " + getArtType().getName() + " with identifier \"" + identifier + "\" found");
        }

        ArtFactory<?, ?, ?, TConfig> factory = factoryOptional.get();
        TConfig config = parseARTConfig(factory, getConfigFieldMap());

        return (TContext) factory.create(config);
    }

    protected TConfig parseARTConfig(ArtFactory<?, ?, ?, ?> factory, Map<String, ConfigFieldInformation> configFieldInformationMap) throws ArtParseException {

        ConfigParser.Result result = null;
        Object artObjectConfig = null;

        ConfigParser actionConfigParser = new ConfigParser(configFieldInformationMap);
        Optional<String> config = getConfig();
        if (config.isPresent() && actionConfigParser.accept(config.get())) {
            result = actionConfigParser.parse();
        }

        if (factory.getConfigClass().isPresent()) {
            try {
                Constructor<?> constructor = factory.getConfigClass().get().getConstructor();
                constructor.setAccessible(true);
                artObjectConfig = constructor.newInstance();
                ConfigParser configParser = new ConfigParser(factory.getConfigInformation());
                String userConfig = getUserConfig();
                if (configParser.accept(userConfig)) {
                    configParser.parse().applyTo(artObjectConfig);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ArtParseException("Unable to find a parameterless public constructor for config type " + factory.getConfigClass().get().getCanonicalName() + ". Make sure your config class is static (if nested) has a public constructor without arguments.", e);
            }
        }

        TConfig artConfig = createConfig(artObjectConfig);

        if (result != null) {
            result.applyTo(artConfig);
        }

        return artConfig;
    }
}
