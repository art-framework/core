package net.silthus.examples.art.requirements;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.art.RequirementContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("EntityLocationRequirement")
class EntityLocationRequirementTest {

    private static ServerMock server;

    @BeforeAll
    static void beforeAll() {
        server = MockBukkit.mock();
    }

    @AfterAll
    static void afterAll() {
        MockBukkit.unmock();
    }

    private final EntityLocationRequirement requirement = new EntityLocationRequirement();

    private Entity withEntityAt(int x, int y, int z, String world) {
        World worldMock = mock(World.class);
        when(worldMock.getName()).thenReturn(world);
        Location location = new Location(worldMock, x, y, z);
        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);
        return entity;
    }

    private Entity withEntityAt(int x, int y, int z) {
        return withEntityAt(x, y, z, "world");
    }

    private RequirementContext<Entity, EntityLocationRequirement.Config> withConfig(EntityLocationRequirement.Config config) {
        RequirementContext<Entity, EntityLocationRequirement.Config> context = mock(RequirementContext.class);
        when(context.getConfig()).thenReturn(Optional.of(config));
        return context;
    }

    private EntityLocationRequirement.Config config(int x, int y, int z, String world) {
        EntityLocationRequirement.Config config = new EntityLocationRequirement.Config();
        config.x = x;
        config.y = y;
        config.z = z;
        config.world = world;
        return config;
    }

    private EntityLocationRequirement.Config config(int x, int y, int z) {
        return config(x, y, z, null);
    }

    @Nested
    @DisplayName("test(...)")
    class test {

        @Test
        @DisplayName("should return true if entity is in same location")
        void shouldReturnTrueIfEntityIsInSameLocation() {

            assertThat(requirement.test(
                    withEntityAt(1, 2, 3, "world"),
                    withConfig(config(1, 2, 3, "world"))))
                    .isTrue();
        }

        @Test
        @DisplayName("should return false if entity is not in the same location")
        void shouldReturnFalseIfNotAtSameLocation() {

            assertThat(requirement.test(
                    withEntityAt(5, 5, 5),
                    withConfig(config(5, 15, 5))
            )).isFalse();
        }

        @Test
        @DisplayName("should return true if config uses wildcard zeros")
        void shouldReturnTrueIfWildCardCoordinatesAreConfigured() {

            assertThat(requirement.test(
                    withEntityAt(20, 56, -105),
                    withConfig(config(0, 56, 0))
            )).isTrue();
        }

        @Test
        @DisplayName("should return false if config uses zeros and has zeros=true flag")
        void shouldReturnFalseWithWildcardsIfZerosIsTrue() {

            EntityLocationRequirement.Config config = config(0, 128, 0);
            config.zeros = true;

            assertThat(requirement.test(
                    withEntityAt(-540, 128, 333),
                    withConfig(config)
            )).isFalse();
        }

        @Test
        @DisplayName("should return true if within radius")
        void shouldReturnTrueIfWithinRadius() {

            EntityLocationRequirement.Config config = config(10, 10, 10);
            config.radius = 10;
            assertThat(requirement.test(
                    withEntityAt(10, 15, 10),
                    withConfig(config)
            )).isTrue();
        }

        @Test
        @DisplayName("should return true if at the radius edge")
        void shouldReturnTrueIfAtEdgeOfRadius() {

            EntityLocationRequirement.Config config = config(10, 10, 10);
            config.radius = 10;
            assertThat(requirement.test(
                    withEntityAt(20, 20, 20),
                    withConfig(config)
            )).isTrue();
        }

        @Test
        @DisplayName("should return false if outside of radius")
        void shouldReturnFalseIfOutsideOfRadius() {

            EntityLocationRequirement.Config config = config(10, 10, 10);
            config.radius = 10;
            assertThat(requirement.test(
                    withEntityAt(21, 20, 20),
                    withConfig(config)
            )).isFalse();
        }
    }
}