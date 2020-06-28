/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.examples.art.requirements;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.art.RequirementContext;
import net.silthus.examples.art.configs.LocationConfig;
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

    private RequirementContext<Entity, LocationConfig> withConfig(LocationConfig config) {
        RequirementContext<Entity, LocationConfig> context = mock(RequirementContext.class);
        when(context.getConfig()).thenReturn(Optional.of(config));
        return context;
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

            LocationConfig config = config(0, 128, 0);
            config.setZeros(true);

            assertThat(requirement.test(
                    withEntityAt(-540, 128, 333),
                    withConfig(config)
            )).isFalse();
        }

        @Test
        @DisplayName("should return true if within radius")
        void shouldReturnTrueIfWithinRadius() {

            LocationConfig config = config(10, 10, 10);
            config.setRadius(10);
            assertThat(requirement.test(
                    withEntityAt(10, 15, 10),
                    withConfig(config)
            )).isTrue();
        }

        @Test
        @DisplayName("should return true if at the radius edge")
        void shouldReturnTrueIfAtEdgeOfRadius() {

            LocationConfig config = config(10, 10, 10);
            config.setRadius(10);
            assertThat(requirement.test(
                    withEntityAt(20, 20, 20),
                    withConfig(config)
            )).isTrue();
        }

        @Test
        @DisplayName("should return false if outside of radius")
        void shouldReturnFalseIfOutsideOfRadius() {

            LocationConfig config = config(10, 10, 10);
            config.setRadius(10);
            assertThat(requirement.test(
                    withEntityAt(21, 20, 20),
                    withConfig(config)
            )).isFalse();
        }
    }
}