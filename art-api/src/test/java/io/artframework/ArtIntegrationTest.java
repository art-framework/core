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

import io.artframework.events.EventHandler;
import io.artframework.events.EventListener;
import io.artframework.events.TriggerEvent;
import io.artframework.integration.actions.DamageAction;
import io.artframework.integration.actions.TextAction;
import io.artframework.integration.data.Entity;
import io.artframework.integration.data.Player;
import io.artframework.integration.requirements.HealthRequirement;
import io.artframework.integration.targets.EntityTarget;
import io.artframework.integration.targets.PlayerTarget;
import io.artframework.integration.trigger.PlayerTrigger;
import lombok.Data;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static io.artframework.Result.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
            @DisplayName("should register trigger from direct instance")
            void shouldRegisterTriggerFromInstance() {

                ART.trigger().add(new PlayerTrigger());

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
                    target.source().setHealth(0);
                    return success();
                });

                assertThat(ART.actions().get("kill"))
                        .isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Events")
    class Events {

        @Test
        @DisplayName("should call trigger event in listener")
        void shouldCallTriggerEventListener() {

            PlayerTrigger trigger = new PlayerTrigger(ART);
            ART.trigger().add(trigger);

            MyEventListener eventListener = spy(new MyEventListener());
            ART.events().register(eventListener);
            eventListener.consumers.add(event -> {
                assertThat(event.getIdentifier()).isEqualTo("move");
            });

            trigger.onMove(new Player());

            verify(eventListener, times(1)).onTrigger(any());
        }
    }

    @Nested
    @DisplayName("ART creation")
    class ArtCreation {

        private PlayerTrigger playerTrigger;

        @BeforeEach
        void setUp() {
            playerTrigger = new PlayerTrigger(ART);
            ART
                    .actions()
                        .add(DamageAction.class)
                        .add(TextAction.class)
                    .requirements()
                        .add(HealthRequirement.class)
                    .trigger()
                        .add(playerTrigger)
                    .targets()
                        .add(Player.class, PlayerTarget::new);
        }

        @AfterEach
        void tearDown() {
            ART.events().unregisterAll();
        }

        @Nested
        @DisplayName("with FlowParser")
        class FlowParser {

            @Test
            @DisplayName("should create list of nested actions")
            void shouldCreateListOfActions() {

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

                assertThat(result.ofTarget(new PlayerTarget(player)))
                        .hasSize(3)
                        .extracting(TargetResult::context)
                        .extracting(ArtObjectContext::options)
                        .extracting(Options::identifier)
                        .contains("damage", "damage", "damage");

            }

            @Test
            @DisplayName("should test requirements before executing actions")
            void shouldTestRequirementsBeforeAction() {

                ArtContext context = ART.builder().load(Arrays.asList(
                        "?health >50",
                        "!damage 40"
                )).build();

                Player foo = new Player("foo");
                foo.setHealth(60);

                assertThat(context.execute(foo).success());
                assertThat(foo.getHealth()).isEqualTo(20);

                assertThat(context.execute(foo).failure());
                assertThat(foo.getHealth()).isEqualTo(20);
            }

            @Test
            @DisplayName("should test requirements of nested actions")
            void shouldTestRequirementsOfNestedActions() {

                ArtContext context = ART.builder().load(Arrays.asList(
                        "?health >50",
                        "!damage 40",
                        "?health >50",
                        "!txt awesome, you, did it"
                )).build();

                Player bar = spy(new Player("bar"));
                bar.setHealth(100);
                Player foo = spy(new Player("foo"));
                foo.setHealth(60);


                bar.getMessageConsumer().add(strings -> assertThat(strings).contains(
                        "awesome",
                        "you",
                        "did it"
                ));
                assertThat(context.execute(bar).success());
                assertThat(bar.getHealth()).isEqualTo(60);
                verify(bar, times(1)).sendMessage(any());

                assertThat(context.execute(foo).failure());
                assertThat(foo.getHealth()).isEqualTo(20);
                verify(foo, never()).sendMessage(any());
            }

            @Test
            @DisplayName("should execute actions after trigger was called")
            void shouldExecuteActionsIfTriggerWasCalled() {

                Player player = new Player();
                player.setHealth(100);

                ART.builder().load(Arrays.asList(
                        "@move",
                        "!damage 40"
                )).build();

                playerTrigger.onMove(player);

                assertThat(player.getHealth()).isEqualTo(60);
            }

            @Test
            @DisplayName("should execute trigger only once")
            void shouldOnlyExecuteTriggerOnce() {

                Player player = new Player();
                player.setHealth(100);

                ART.builder().load(Arrays.asList(
                        "@move(execute_once=true)",
                        "!damage 40"
                )).build();

                playerTrigger.onMove(player);
                playerTrigger.onMove(player);
                playerTrigger.onMove(player);

                assertThat(player.getHealth()).isEqualTo(60);
            }

            @Test
            @DisplayName("should execute actions on both triggers")
            void shouldExecuteOneOrTheOtherTrigger() {

                Player player = new Player();
                player.setHealth(100);

                ART.builder().load(Arrays.asList(
                        "@move",
                        "@damage",
                        "!damage 40"
                )).build();

                playerTrigger.onMove(player);
                playerTrigger.onDamage(player);

                assertThat(player.getHealth()).isEqualTo(20);
            }
        }
    }

    @Data
    public static class MyEventListener implements EventListener {

        private final List<Consumer<TriggerEvent>> consumers = new ArrayList<>();

        @EventHandler
        public void onTrigger(TriggerEvent event) {
            consumers.forEach(triggerEventConsumer -> triggerEventConsumer.accept(event));
        }
    }
}
