package io.artframework.bukkit.requirements;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.artframework.ExecutionContext;
import io.artframework.RequirementContext;
import io.artframework.Target;
import io.artframework.bukkit.targets.EntityTarget;
import io.artframework.bukkit.trigger.configs.LocationConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
class LocationRequirementTest {

    private static ServerMock server;
    private LocationRequirement requirement;

    @BeforeAll
    static void beforeAll() {

        server = MockBukkit.mock();
    }

    @BeforeEach
    void setUp() {

        requirement = new LocationRequirement();
    }

    @AfterAll
    static void afterAll() {

        MockBukkit.unmock();
    }

    private Target<Entity> withEntityAt(int x, int y, int z, String world) {
        World worldMock = mock(World.class);
        when(worldMock.getName()).thenReturn(world);
        Location location = new Location(worldMock, x, y, z);
        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);
        return new EntityTarget(entity);
    }

    private Target<Entity> withEntityAt(int x, int y, int z) {
        return withEntityAt(x, y, z, "world");
    }

    private LocationConfig config(int x, int y, int z, String world) {
        LocationConfig config = new LocationConfig();
        config.setX(x);
        config.setY(y);
        config.setZ(z);
        config.setWorld(world);
        return config;
    }

    private LocationConfig config(int x, int y, int z) {
        return config(x, y, z, null);
    }

    private ExecutionContext<RequirementContext<Entity>> withConfig(LocationConfig config) {

        requirement.load(config);
        return withContext();
    }

    private ExecutionContext<RequirementContext<Entity>> withContext() {

        return (ExecutionContext<RequirementContext<Entity>>) mock(ExecutionContext.class);
    }

    @Nested
    @DisplayName("test(...)")
    class test {

        @Test
        @DisplayName("should return true if entity is in same location")
        void shouldReturnTrueIfEntityIsInSameLocation() {

            assertThat(requirement.test(
                    withEntityAt(1, 2, 3, "world"),
                    withConfig(config(1, 2, 3, "world"))).success())
                    .isEqualTo(true);
        }

        @Test
        @DisplayName("should return false if entity is not in the same location")
        void shouldReturnFalseIfNotAtSameLocation() {

            assertThat(requirement.test(
                    withEntityAt(5, 5, 5),
                    withConfig(config(5, 15, 5))
            ).success()).isFalse();
        }

        @Test
        @DisplayName("should return true if config uses wildcard zeros")
        void shouldReturnTrueIfWildCardCoordinatesAreConfigured() {

            assertThat(requirement.test(
                    withEntityAt(20, 56, -105),
                    withConfig(config(0, 56, 0))
            ).success()).isTrue();
        }

        @Test
        @DisplayName("should return false if config uses zeros and has zeros=true flag")
        void shouldReturnFalseWithWildcardsIfZerosIsTrue() {

            LocationConfig config = config(0, 128, 0);
            config.setZeros(true);

            assertThat(requirement.test(
                    withEntityAt(-540, 128, 333),
                    withConfig(config)
            ).success()).isFalse();
        }

        @Test
        @DisplayName("should return true if within radius")
        void shouldReturnTrueIfWithinRadius() {

            LocationConfig config = config(10, 10, 10);
            config.setRadius(10);

            assertThat(requirement.test(
                    withEntityAt(10, 15, 10),
                    withConfig(config)
            ).success()).isTrue();
        }

        @Test
        @DisplayName("should return true if at the radius edge")
        void shouldReturnTrueIfAtEdgeOfRadius() {

            LocationConfig config = config(10, 10, 10);
            config.setRadius(10);

            assertThat(requirement.test(
                    withEntityAt(20, 20, 20),
                    withConfig(config)
            ).success()).isTrue();
        }

        @Test
        @DisplayName("should return false if outside of radius")
        void shouldReturnFalseIfOutsideOfRadius() {

            LocationConfig config = config(10, 10, 10);
            config.setRadius(10);

            assertThat(requirement.test(
                    withEntityAt(21, 20, 20),
                    withConfig(config)
            ).success()).isFalse();
        }
    }
}