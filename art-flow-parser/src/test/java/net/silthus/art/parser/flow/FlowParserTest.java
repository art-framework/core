package net.silthus.art.parser.flow;

import com.google.inject.Provider;
import net.silthus.art.api.parser.flow.ArtTypeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("FlowParser")
class FlowParserTest {

    private FlowParser parser;
    private Set<Provider<ArtTypeParser<?>>> parsers = new HashSet<>();

    @BeforeEach
    void beforeEach() {
        parser = new FlowParser(parsers);
    }

    @Nested
    @DisplayName("next(Object)")
    class parse {

        @Test
        @DisplayName("should throw if config object is null")
        public void shouldThrowIfObjectIsNull() {
            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> parser.parse(null));
        }
    }
}