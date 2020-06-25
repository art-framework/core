package net.silthus.art;

import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.requirements.RequirementManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("ArtManager")
class DefaultArtManagerTest {

    private DefaultArtManager artManager;
    private ActionManager actionManager;
    private RequirementManager requirementManager;
    private final ArtModuleDescription description = new ArtModuleDescription("test", "1.0.0");

    @BeforeEach
    void beforeEach() {
        actionManager = mock(ActionManager.class);
        requirementManager = mock(RequirementManager.class);
        artManager = new DefaultArtManager(actionManager, requirementManager, new HashMap<>());
    }

    @Nested
    @DisplayName("register(ArtModuleDescription, Consumer<ArtBuilder>)")
    class register {

        @Test
        @DisplayName("should register the plugin as loaded")
        void shouldRegisterThePluginAsLoaded() {

            artManager.register(description, artBuilder -> {
            });

            assertThat(artManager.getRegisteredPlugins())
                    .containsKeys(description);
        }

        private ArtBuilder builder;

        @Test
        @DisplayName("should reuse the same builder for the same plugin")
        void shouldReuseBuilderForSamePlugin() {

            artManager.register(description, artBuilder -> {
                builder = artBuilder;
            });
            artManager.register(description, artBuilder -> {
                assertThat(artBuilder).isSameAs(builder);
            });
        }

        @Test
        @DisplayName("should register all requirements")
        void shouldRegisterRequirements() {

            artManager.register(description, artBuilder -> artBuilder
                    .requirement(String.class, (s, context) -> true).withName("foobar")
                    .requirement(Integer.class, (integer, context) -> false).withName("int")
            );

            verify(requirementManager, times(1)).register(anyMap());
        }
    }

}