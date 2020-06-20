package net.silthus.art.parser.flow.types;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.Getter;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.flow.Parser;
import net.silthus.art.util.ConfigUtil;
import net.silthus.art.util.ReflectionUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigParser extends Parser<ConfigParser.Result> {

    @Getter
    private final Map<String, ConfigFieldInformation> configMap;

    public ConfigParser(Map<String, ConfigFieldInformation> configMap) {
        // regexr.com/56s0f
        super(Pattern.compile("^(?<keyValue>((?<key>[\\w\\d.]+)?[:=] ?)?(?<value>[\\w\\d. !?&$/\\\\\"]+))([,;] ?(?<config>.*))?$"));
        this.configMap = ImmutableMap.copyOf(configMap);
    }

    @Override
    public Result parse() throws ArtParseException {

        Map<ConfigFieldInformation, Object> fieldValueMap = new HashMap<>();
        List<KeyValuePair> keyValuePairs = extractKeyValuePairs(getMatcher());
        Set<ConfigFieldInformation> mappedFields = new HashSet<>();

        for (int i = 0; i < keyValuePairs.size(); i++) {
            KeyValuePair keyValue = keyValuePairs.get(i);
            ConfigFieldInformation configFieldInformation;
            if (keyValue.getKey().isPresent() && getConfigMap().containsKey(keyValue.getKey().get())) {
                configFieldInformation = getConfigMap().get(keyValue.getKey().get());
            } else if (getConfigMap().size() == 1) {
                Optional<ConfigFieldInformation> fieldInformation = getConfigMap().values().stream().findFirst();
                if (fieldInformation.isEmpty()) {
                    throw new ArtParseException("Config should only defines one parameter, but none was found.");
                }
                configFieldInformation = fieldInformation.get();
            } else {
                int finalI = i;
                Optional<ConfigFieldInformation> optionalFieldInformation = getConfigMap().values().stream().filter(info -> info.getPosition() == finalI).findFirst();
                if (optionalFieldInformation.isEmpty()) {
                    throw new ArtParseException("Config does not define positioned parameters. Use key value pairs instead.");
                }
                configFieldInformation = optionalFieldInformation.get();
            }

            if (keyValue.getValue().isEmpty()) {
                throw new ArtParseException("Config " + configFieldInformation.getIdentifier() + " has an empty value.");
            }

            Object value = ReflectionUtil.toObject(configFieldInformation.getType(), keyValue.getValue().get());

            fieldValueMap.put(configFieldInformation, value);
            mappedFields.add(configFieldInformation);
        }

        List<ConfigFieldInformation> missingRequiredFields = getConfigMap().values().stream()
                .filter(ConfigFieldInformation::isRequired)
                .filter(configFieldInformation -> !mappedFields.contains(configFieldInformation))
                .collect(Collectors.toList());

        if (!missingRequiredFields.isEmpty()) {
            throw new ArtParseException("Config is missing " + missingRequiredFields.size() + " required fields: "
                    + missingRequiredFields.stream().map(ConfigFieldInformation::getIdentifier).collect(Collectors.joining(",")));
        }

        return new Result(fieldValueMap);
    }

    protected List<KeyValuePair> extractKeyValuePairs(Matcher matcher) {

        ArrayList<KeyValuePair> pairs = new ArrayList<>();

        pairs.add(new KeyValuePair(matcher.group("key"), matcher.group("value")));

        String config = matcher.group("config");
        if (!Strings.isNullOrEmpty(config)) {
            matcher = getPattern().matcher(config.trim());
            if (matcher.matches()) {
                pairs.addAll(extractKeyValuePairs(matcher));
            }
        }

        return pairs;
    }

    @Data
    static class KeyValuePair {

        private final String key;
        private final String value;

        public Optional<String> getKey() {
            return Optional.ofNullable(key);
        }

        public Optional<String> getValue() {
            return Optional.ofNullable(value);
        }
    }

    public static class Result {

        private final Map<ConfigFieldInformation, Object> keyValueMap;

        private Result(Map<ConfigFieldInformation, Object> keyValueMap) {
            this.keyValueMap = ImmutableMap.copyOf(keyValueMap);
        }

        public <TConfig> TConfig applyTo(TConfig config) {
            ConfigUtil.setConfigFields(config, keyValueMap);
            return config;
        }
    }

}
