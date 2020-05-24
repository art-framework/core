package net.silthus.art.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ARTBuilder")
public class ARTBuilderTest {

    @Test
    @DisplayName("should create a list of art factories")
    public void shouldCreateActionFactories() {

        ARTBuilder builder = new ARTBuilder();
        builder.action(String.class, String.class, (s, context) -> {})
                .target(Integer.class)
                    .action(String.class, (integer, context) -> {});

        assertThat(builder.build())
                .hasSize(2);
    }
}