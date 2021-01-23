package io.artframework.conf;

import io.artframework.ART;
import io.artframework.ConfigMap;
import io.artframework.ConfigurationException;
import io.artframework.annotations.ConfigOption;
import io.artframework.util.ConfigUtil;
import io.artframework.util.ConfigUtilTest;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ALL")
@Nested
public
class ConfigMapTest {

    @Test
    @DisplayName("should load all fields including superclass")
    public void shouldLoadAllFields() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .hasSizeGreaterThanOrEqualTo(4)
                .containsKeys(
                        "parent_field",
                        "required",
                        "default_field",
                        "all_annotations"
                )
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should load required annotation")
    public void shouldLoadRequiredAttribute() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .extractingByKey("required")
                .extracting(ConfigFieldInformation::required)
                .isEqualTo(true)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should load description annotation")
    public void shouldLoadDescriptionAttribute() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .extractingByKey("default_field")
                .extracting(ConfigFieldInformation::description)
                .isEqualTo(new String[]{"World to teleport the player to."})
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should load default value")
    public void shouldLoadDefaultValue() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .extractingByKey("default_field")
                .extracting(ConfigFieldInformation::defaultValue)
                .isEqualTo("world")
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should load required field with default value")
    public void shouldLoadRequiredDefaultValue() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .extractingByKey("all_annotations")
                .extracting(ConfigFieldInformation::defaultValue, ConfigFieldInformation::description)
                .contains(2.0d, new String[]{"Required field with default value."})
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should load nested config objects")
    public void shouldLoadNestedObjects() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .extractingByKey("nested.nested_field")
                .extracting(ConfigFieldInformation::description, ConfigFieldInformation::defaultValue)
                .contains(new String[]{"nested config field"}, "foobar")
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should not load nested object fields")
    public void shouldNotAddNestedBase() {
        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .doesNotContainKey("nested")
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should ignore fields without an annotation")
    public void shouldIgnoredIgnored() {
        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .doesNotContainKeys("ignored", "no_annotations")
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should load field position annotation")
    public void shouldLoadFieldPosition() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.TestConfig.class))
                .extractingByKeys("required", "parent_field")
                .extracting(ConfigFieldInformation::position)
                .contains(1, 0)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should throw if same field position is found")
    public void shouldThrowExceptionForSamePosition() {

        assertThatExceptionOfType(ConfigurationException.class)
                .isThrownBy(() -> ConfigUtil.getConfigFields(ConfigUtilTest.SamePositionConfig.class))
                .withMessageContaining("same position");
    }

    @Test
    @DisplayName("should throw if declared config field is final")
    void shouldThrowIfConfigOptionIsFinal() {

        assertThatExceptionOfType(ConfigurationException.class)
                .isThrownBy(() -> ConfigUtil.getConfigFields(ConfigUtilTest.FinalConfig.class))
                .withMessageContaining("final field");
    }

    @Test
    @DisplayName("should load all fields if the class is annotated")
    void shouldLoadAllFieldsInAnnotatedClass() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.AnnotatedClass.class))
                .containsOnlyKeys("foo", "bar")
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should load array field")
    void shouldLoadArrayField() {

        assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ConfigUtilTest.ArrayConfig.class))
                .containsOnlyKeys("foo", "array")
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should ignored unmapped key value pairs")
    void shouldIgnoreUnmappedKeyValuePairs() throws ConfigurationException {

        SingleFieldConfig config = ConfigMap.of(SingleFieldConfig.class)
                .with(Arrays.asList(
                        KeyValuePair.of("test", "foobar"),
                        KeyValuePair.of("foo", "bar"),
                        KeyValuePair.of("num", "1")
                )).applyTo(ART.globalScope(), new SingleFieldConfig());

        assertThat(config)
                .extracting(SingleFieldConfig::getTest)
                .isEqualTo("foobar");
    }

    @Test
    @DisplayName("should use first value if all are without matching keys")
    void shouldUseFirstValueForMultipleWithoutKeys() throws ConfigurationException {

        SingleFieldConfig config = ConfigMap.of(SingleFieldConfig.class)
                .with(Arrays.asList(
                        KeyValuePair.of(null, "foobar"),
                        KeyValuePair.of(null, "bar"),
                        KeyValuePair.of(null, "1")
                )).applyTo(ART.globalScope(), new SingleFieldConfig());

        assertThat(config)
                .extracting(SingleFieldConfig::getTest)
                .isEqualTo("foobar");
    }

    @Test
    @DisplayName("should not map keys that do not match")
    void shouldNotMapUnmatchingKey() throws ConfigurationException {

        SingleFieldConfig config = ConfigMap.of(SingleFieldConfig.class)
                .with(Arrays.asList(
                        KeyValuePair.of("f", "foobar"),
                        KeyValuePair.of("a", "bar"),
                        KeyValuePair.of("b", "1")
                )).applyTo(ART.globalScope(), new SingleFieldConfig());

        assertThat(config)
                .extracting(SingleFieldConfig::getTest)
                .isNull();
    }

    @Data
    public static class SingleFieldConfig {

        @ConfigOption
        private String test;
    }
}
