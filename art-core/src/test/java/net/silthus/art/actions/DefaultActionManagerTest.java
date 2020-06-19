package net.silthus.art.actions;

import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("DefaultActionManager")
class DefaultActionManagerTest {

    private DefaultActionManager actionManager;

    @BeforeEach
    void beforeEach() {
        this.actionManager = new DefaultActionManager(new HashMap<>());
        actionManager.setLogger(Logger.getGlobal());
    }

    @Nested
    @DisplayName("exists(String)")
    class exists {

        @Test
        @DisplayName("should return false if action is not registered")
        void shouldNotExist() {

            assertThat(actionManager.exists("foobar"))
                    .isFalse();
        }

        @Test
        @DisplayName("should return true if action was registered")
        void shouldExist() {
            actionManager.getActionFactories().put("foobar", mock(ActionFactory.class));

            assertThat(actionManager.exists("foobar"))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("register(...)")
    class register {

        @Test
        @DisplayName("should store registered actions in memory")
        void shouldAddActionsToMemory() {

            actionManager.register(Map.of("foobar", mock(ActionFactory.class)));

            assertThat(actionManager.exists("foobar")).isTrue();
        }

        @Test
        @DisplayName("should not register actions with a duplicate identifier")
        void shouldNotAddDuplicateActions() {

            ActionFactory mock = mock(ActionFactory.class);
            when(mock.getIdentifier()).thenReturn("foobar1");
            when(mock.getArtObject()).thenReturn(mock(Action.class));
            actionManager.register(Map.of("foobar", mock));

            assertThat(actionManager.getActionFactories().get("foobar"))
                    .isNotNull()
                    .extracting("identifier")
                    .isEqualTo("foobar1");

            ActionFactory mock2 = mock(ActionFactory.class);
            when(mock2.getIdentifier()).thenReturn("foobar2");
            when(mock2.getArtObject()).thenReturn(mock(Action.class));
            actionManager.register(Map.of("foobar", mock2));

            assertThat(actionManager.getActionFactories().get("foobar"))
                    .isNotNull()
                    .extracting("identifier")
                    .isNotEqualTo("foobar2");
        }

        @Test
        @DisplayName("should register all actions inside the map")
        void shouldAddAllActionsInTheMap() {

            actionManager.register(Map.of(
                    "test1", mock(ActionFactory.class),
                    "test2", mock(ActionFactory.class),
                    "test3", mock(ActionFactory.class),
                    "test4", mock(ActionFactory.class),
                    "test5", mock(ActionFactory.class)
            ));

            assertThat(actionManager.getActionFactories())
                    .hasSize(5)
                    .containsKeys("test1", "test2","test3","test4","test5");
        }
    }

    @Nested
    @DisplayName("getFactory(...)")
    class getFactory {

        @Test
        @DisplayName("should return wrapped optional")
        void shouldReturnWrappedOptional() {

            actionManager.getActionFactories().put("foobar", mock(ActionFactory.class));

            assertThat(actionManager.getFactory("foobar"))
                    .isNotEmpty();
        }

        @Test
        @DisplayName("should return empty optional if action factory does not exist")
        void shouldReturnEmptyOptional() {

            assertThat(actionManager.getFactory("foobar"))
                    .isEmpty();
        }
    }
}