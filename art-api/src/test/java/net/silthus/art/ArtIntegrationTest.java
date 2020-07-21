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

package net.silthus.art;

import net.silthus.art.integration.actions.DamageAction;
import net.silthus.art.integration.data.Entity;
import net.silthus.art.integration.data.Player;
import net.silthus.art.integration.requirements.HealthRequirement;
import net.silthus.art.integration.targets.EntityTarget;
import net.silthus.art.integration.targets.PlayerTarget;
import net.silthus.art.integration.trigger.PlayerTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
@DisplayName("ART Integration Tests")
public class ArtIntegrationTest {

    private Configuration ART;

    @BeforeEach
    void setUp() {
        ART = Configuration.create();
    }

    @Nested
    @DisplayName("ART registration of")
    class ArtRegistration {

        @Nested
        @DisplayName("targets")
        class Targets {

            @Test
            @DisplayName("should register direct matching targets")
            void targets() {

                ART.targets()
                        .add(Player.class, PlayerTarget::new)
                        .add(Entity.class, EntityTarget::new);

                assertThat(ART.targets().exists(new Player("foo"))).isTrue();
                assertThat(ART.targets().exists(new Entity("foo"))).isTrue();

                assertThat(ART.targets().get(new Player("foo")))
                        .isNotEmpty().get()
                        .isExactlyInstanceOf(PlayerTarget.class);
                assertThat(ART.targets().get(new Entity("foo")))
                        .isNotEmpty().get()
                        .isExactlyInstanceOf(EntityTarget.class);
            }

            @Test
            @DisplayName("should use closest target type")
            void shouldUseClosestTarget() {

                ART.targets().add(Entity.class, EntityTarget::new);

                assertThat(ART.targets().get(new Player("foo")))
                        .isNotEmpty().get()
                        .isExactlyInstanceOf(EntityTarget.class);
            }

            @Test
            @DisplayName("should return empty if no target exists")
            void shouldReturnEmptyIfNoneExists() {

                assertThat(ART.targets().get("foo")).isEmpty();
            }
        }

        @Nested
        @DisplayName("actions, requirements and trigger")
        class ActionsRequirementsTrigger {

            @Test
            @DisplayName("should register actions")
            void shouldRegisterActions() {

                ART.actions().add(DamageAction.class);

                assertThat(ART.actions().get("damage"))
                        .isNotEmpty().get()
                        .extracting(ArtFactory::info)
                        .extracting(ArtInformation::getIdentifier, ArtInformation::getAlias, ArtInformation::getArtObjectClass)
                        .contains("damage", new String[]{"hit", "dmg"}, DamageAction.class);

            }

            @Test
            @DisplayName("should register actions")
            void shouldRegisterRequirements() {

                ART.requirements().add(HealthRequirement.class);

                assertThat(ART.requirements().get("health"))
                        .isNotEmpty().get()
                        .extracting(ArtFactory::info)
                        .extracting(ArtInformation::getIdentifier, ArtInformation::getAlias, ArtInformation::getArtObjectClass)
                        .contains("health", new String[]{"hit", "dmg"}, HealthRequirement.class);

            }

            @Test
            @DisplayName("should register trigger")
            void shouldRegisterTrigger() {

                ART.trigger().add(PlayerTrigger.class);

//                assertThat(ART.trigger().("move"))
//                        .isNotEmpty().get()
//                        .extracting(ArtFactory::info)
//                        .extracting(ArtInformation::getIdentifier, ArtInformation::getAlias, ArtInformation::getArtObjectClass)
//                        .contains("health", new String[]{"hit", "dmg"}, HealthRequirement.class);

            }
        }
    }
}
