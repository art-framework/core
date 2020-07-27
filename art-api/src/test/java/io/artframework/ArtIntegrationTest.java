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

package io.artframework;

import io.artframework.integration.actions.DamageAction;
import io.artframework.integration.data.Entity;
import io.artframework.integration.data.Player;
import io.artframework.integration.requirements.HealthRequirement;
import io.artframework.integration.targets.EntityTarget;
import io.artframework.integration.targets.PlayerTarget;
import io.artframework.integration.trigger.PlayerTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.artframework.Result.success;
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
                        .extracting(ArtFactory::options)
                        .extracting(Options::identifier, Options::alias, Options::artObjectClass)
                        .contains("damage", new String[]{"hit", "dmg"}, DamageAction.class);

            }

            @Test
            @DisplayName("should register actions")
            void shouldRegisterRequirements() {

                ART.requirements().add(HealthRequirement.class);

                assertThat(ART.requirements().get("health"))
                        .isNotEmpty().get()
                        .extracting(ArtFactory::options)
                        .extracting(Options::identifier, Options::alias, Options::artObjectClass)
                        .contains("health", new String[0], HealthRequirement.class);

            }

            @Test
            @DisplayName("should register trigger")
            void shouldRegisterTrigger() {

                ART.trigger().add(PlayerTrigger.class);

                assertThat(ART.trigger().get("move"))
                        .isNotEmpty();

                assertThat(ART.trigger().get("damage"))
                        .isNotEmpty();

                assertThat(ART.trigger().get("dmg"))
                        .isNotEmpty().get()
                        .extracting(triggerFactory -> triggerFactory.options())
                        .extracting(triggerArtInformation -> triggerArtInformation.identifier())
                        .isEqualTo("damage");
            }

            @Test
            @DisplayName("should register lambda action")
            void shouldRegisterLambda() {

                ART.actions().add("kill", Player.class, (target, context) -> {
                    target.getSource().setHealth(0);
                    return success();
                });

                assertThat(ART.actions().get("kill"))
                        .isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("ART creation")
    class ArtCreation {

        @Nested
        @DisplayName("with FlowParser")
        class FlowParser {

            @Test
            @DisplayName("should create list of nested actions")
            void shouldCreateListOfActions() {

                ART.actions()
                        .add(DamageAction.class)
                    .targets()
                        .add(Player.class, PlayerTarget::new);

                ArtContext context = ART.builder().load(Arrays.asList(
                        "!damage 20",
                        "!dmg 50",
                        "!hit 10"
                )).build();

                Player player = new Player();
                player.setHealth(100);

                CombinedResult result = context.execute(player);

                assertThat(result.success()).isTrue();
                assertThat(player.getHealth()).isEqualTo(20);

                assertThat(result.ofTarget(player))
                        .hasSize(3)
                        .extracting(TargetResult::options)
                        .extracting(Options::identifier)
                        .contains("damage", "damage", "damage");

            }

        }
    }
}
