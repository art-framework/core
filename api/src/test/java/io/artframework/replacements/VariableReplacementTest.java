package io.artframework.replacements;

import io.artframework.ART;
import io.artframework.ExecutionContext;
import io.artframework.Variable;
import io.artframework.impl.ReplacementContext;
import io.artframework.integration.data.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VariableReplacementTest {

    private VariableReplacement replacement;
    private ReplacementContext context;
    private final Map<String, Variable<?>> variables = new HashMap<>();

    @BeforeEach
    void setUp() {

        replacement = new VariableReplacement();
        ExecutionContext<?> context = mock(ExecutionContext.class);
        when(context.variables()).thenAnswer(invocation -> variables);
        this.context = new ReplacementContext(ART.globalScope(), null, context);
    }

    @Test
    @DisplayName("should replace single variable")
    void shouldReplaceSingleVariable() {

        variables.put("player", Variable.of("player", "foobar"));

        assertThat(replacement.replace("${player}", context))
                .isEqualTo("foobar");
    }

    @Test
    @DisplayName("should ignore variables that do not match")
    void shouldIgnoreUnmatchingVariables() {

        variables.put("player", Variable.of("player", "foobar"));

        assertThat(replacement.replace("${play} foo bar", context))
                .isEqualTo("${play} foo bar");
    }

    @Test
    @DisplayName("should replace multiple variable matches")
    void shouldReplaceAllMatchingVariables() {

        variables.put("player", Variable.of("player", "bar"));

        assertThat(replacement.replace("${player} foo ${player}", context))
                .isEqualTo("bar foo bar");
    }

    @Test
    @DisplayName("should replace multiple variables")
    void shouldReplaceMultipleVariables() {

        variables.put("player", Variable.of("player", "me"));
        variables.put("foo", Variable.of("foo", "bar"));

        assertThat(replacement.replace("${player} foo ${foo}", context))
                .isEqualTo("me foo bar");
    }

    @Test
    @DisplayName("should not replace variables that are not a string")
    void shouldNotReplaceVariablesThatAreNotAString() {

        variables.put("player", Variable.of("player", new Player()));
        variables.put("foo", Variable.of("foo", "bar"));

        assertThat(replacement.replace("${player} foo ${foo}", context))
                .isEqualTo("${player} foo bar");
    }

    @Test
    @DisplayName("should replace primitive values as string")
    void shouldReplacePrimitiveValues() {

        variables.put("int", Variable.of("int", 1));
        variables.put("double", Variable.of("double", 2.0d));
        variables.put("float", Variable.of("float", 3f));
        variables.put("long", Variable.of("long", 4L));
        variables.put("boolean", Variable.of("boolean", true));

        assertThat(replacement.replace("${int} ${double} ${float} ${long} ${boolean}", context))
                .isEqualTo("1 2.0 3.0 4 true");
    }
}