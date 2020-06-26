package net.silthus.art.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigFieldInformation")
class ConfigFieldInformationTest {

    private ConfigFieldInformation fieldInfo(String name) {
        return new ConfigFieldInformation(name, name, String.class);
    }

    private ConfigFieldInformation fieldInfo(String name, int position) {
        ConfigFieldInformation info = fieldInfo(name);
        info.setPosition(position);
        return info;
    }

    @Nested
    @DisplayName("compare(...)")
    class compare {

        @Test
        @DisplayName("should sort by position in ascending order")
        void shouldSortByPosition() {

            List<ConfigFieldInformation> fields = List.of(
                    fieldInfo("bar", 1),
                    fieldInfo("test", 3),
                    fieldInfo("foo", 0),
                    fieldInfo("aaaa", 2)
            );

            ArrayList<ConfigFieldInformation> sorted = new ArrayList<>(fields);
            sorted.sort(ConfigFieldInformation::compareTo);

            assertThat(sorted).extracting(ConfigFieldInformation::getPosition)
                    .containsExactly(0, 1, 2, 3);
        }

        @Test
        @DisplayName("should sort unpositioned elements at the end")
        void shouldSortUnpositionedElementsAtTheEnd() {

            List<ConfigFieldInformation> fields = List.of(
                    fieldInfo("bar", 1),
                    fieldInfo("test", 3),
                    fieldInfo("abc"),
                    fieldInfo("foo", 0),
                    fieldInfo("unsorted"),
                    fieldInfo("aaaa", 2)
            );

            ArrayList<ConfigFieldInformation> sorted = new ArrayList<>(fields);
            sorted.sort(ConfigFieldInformation::compareTo);

            assertThat(sorted).extracting(ConfigFieldInformation::getPosition)
                    .containsExactly(0, 1, 2, 3, -1, -1);
        }

        @Test
        @DisplayName("should sort unpositioned elements by name and at the end")
        void shouldSortUnpositionedElementsByName() {

            List<ConfigFieldInformation> fields = List.of(
                    fieldInfo("zzz"),
                    fieldInfo("bar", 1),
                    fieldInfo("test", 3),
                    fieldInfo("abc"),
                    fieldInfo("foo", 0),
                    fieldInfo("unsorted"),
                    fieldInfo("aaaa", 2)
            );

            ArrayList<ConfigFieldInformation> sorted = new ArrayList<>(fields);
            sorted.sort(ConfigFieldInformation::compareTo);

            assertThat(sorted).extracting(ConfigFieldInformation::getName)
                    .containsExactly("foo", "bar", "aaaa", "test", "abc", "unsorted", "zzz");
        }
    }
}