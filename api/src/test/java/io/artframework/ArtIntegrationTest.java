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

import io.artframework.impl.DefaultScope;
import io.artframework.integration.ExternalTestModule;
import io.artframework.integration.actions.DamageAction;
import io.artframework.integration.actions.TestGenericAction;
import io.artframework.integration.actions.TextAction;
import io.artframework.integration.data.Entity;
import io.artframework.integration.data.Player;
import io.artframework.integration.requirements.HealthRequirement;
import io.artframework.integration.targets.EntityTarget;
import io.artframework.integration.targets.PlayerTarget;
import io.artframework.integration.trigger.PlayerDamangeTrigger;
import io.artframework.integration.trigger.PlayerMoveTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static io.artframework.Result.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
@DisplayName("ART Integration Tests")
public class ArtIntegrationTest {

    private Scope scope;

    @BeforeEach
    void setUp() {

        scope = new DefaultScope();
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

                TargetProvider targets = scope.configuration().targets();
                targets
                        .add(Player.class, PlayerTarget::new)
                        .add(Entity.class, EntityTarget::new);

                assertThat(targets.exists(new Player("foo"))).isTrue();
                assertThat(targets.exists(new Entity("foo"))).isTrue();

                assertThat(targets.get(new Player("foo")))
                        .isNotEmpty().get()
                        .isExactlyInstanceOf(PlayerTarget.class);
                assertThat(targets.get(new Entity("foo")))
                        .isNotEmpty().get()
                        .isExactlyInstanceOf(EntityTarget.class);
            }

            @Test
            @DisplayName("should use closest target type")
            void shouldUseClosestTarget() {

                TargetProvider targets = scope.configuration().targets();
                targets.add(Entity.class, EntityTarget::new);

                assertThat(targets.get(new Player("foo")))
                        .isNotEmpty().get()
                        .isExactlyInstanceOf(EntityTarget.class);
            }

            @Test
            @DisplayName("should return empty if no target exists")
            void shouldReturnEmptyIfNoneExists() {

                assertThat(scope.configuration().targets().get("foo")).isEmpty();
            }
        }

        @Nested
        @DisplayName("actions, requirements and trigger")
        class ActionsRequirementsTrigger {

            @Test
            @DisplayName("should register actions")
            void shouldRegisterActions() {

                ActionProvider actions = scope.configuration().actions();
                actions.add(DamageAction.class);

                assertThat(actions.get("damage"))
                        .isNotEmpty().get()
                        .extracting(Factory::meta)
                        .extracting(ArtObjectMeta::identifier, ArtObjectMeta::alias, ArtObjectMeta::artObjectClass)
                        .contains("damage", new String[]{"hit", "dmg"}, DamageAction.class);

            }

            @Test
            @DisplayName("should register requirements")
            void shouldRegisterRequirements() {

                scope.configuration().requirements().add(HealthRequirement.class);

                assertThat(scope.configuration().requirements().get("health"))
                        .isNotEmpty().get()
                        .extracting(Factory::meta)
                        .extracting(ArtObjectMeta::identifier, ArtObjectMeta::alias, ArtObjectMeta::artObjectClass)
                        .contains("health", new String[0], HealthRequirement.class);
            }

            @Test
            @DisplayName("should register generic actions")
            void shouldRegisterGenericAction() {

                scope.configuration().actions().add(TestGenericAction.class);

                assertThat(scope.configuration().actions().get("test-generic-action"))
                        .isNotEmpty();
            }

            @Test
            @DisplayName("should register trigger")
            void shouldRegisterTrigger() {

                TriggerProvider trigger = scope.configuration().trigger();
                trigger.add(PlayerDamangeTrigger.class);
                trigger.add(PlayerMoveTrigger.class);

                assertThat(trigger.get("move"))
                        .isNotEmpty();

                assertThat(trigger.get("damage"))
                        .isNotEmpty();

                assertThat(trigger.get("dmg"))
                        .isNotEmpty().get()
                        .extracting(triggerFactory -> triggerFactory.meta())
                        .extracting(triggerArtInformation -> triggerArtInformation.identifier())
                        .isEqualTo("damage");
            }

            @Test
            @DisplayName("should register lambda action")
            void shouldRegisterLambda() {

                ActionProvider actions = scope.configuration().actions();
                actions.add("kill", Player.class, (target, context) -> {
                    target.source().setHealth(0);
                    return success();
                });

                assertThat(actions.get("kill"))
                        .isNotEmpty();
            }
        }

        @Nested
        @DisplayName("Modules")
        class Modules {

            @Test
            @DisplayName("should register module and call bootstrap method")
            void shouldRegisterExternalTestModuleAndCallBootstrap() throws Exception {

                ExternalTestModule module = spy(new ExternalTestModule());

                scope.register(module);

                assertThat(scope.configuration().modules().all())
                        .anyMatch(moduleMeta -> moduleMeta.moduleClass().equals(ExternalTestModule.class));

                verify(module, times(1)).onBootstrap((BootstrapScope) scope);
            }
        }
    }

    @Nested
    @DisplayName("Trigger")
    class Events {

        @Test
        @DisplayName("should call registered triggers")
        void shouldCallRegisteredTrigger() throws ParseException {

            scope.configuration().targets().add(Player.class, PlayerTarget::new);

            scope.configuration().trigger().add(PlayerMoveTrigger.class);

            ArtContext context = scope.load(Arrays.asList("@move"));
            context.enableTrigger();

            Player player = new Player("foo");
            Optional<Target<Player>> playerTarget = scope.configuration().targets().get(player);

            TriggerListener<Player> triggerListener = spy(new TriggerListener<Player>() {
                @Override
                public void onTrigger(Target<Player> target, ExecutionContext<TriggerContext> context) {
                    target.equals(playerTarget.get());
                }
            });

            context.onTrigger(Player.class, triggerListener);

            scope.trigger(PlayerMoveTrigger.class).with(player).execute();

            verify(triggerListener, times(1)).onTrigger(any(), any());
        }
    }

    @Nested
    @DisplayName("ART creation")
    class ArtCreation {

        @BeforeEach
        void setUp() {
            scope.configuration()
                    .actions()
                        .add(DamageAction.class)
                        .add(TextAction.class)
                    .requirements()
                        .add(HealthRequirement.class)
                    .trigger()
                        .add(PlayerDamangeTrigger.class)
                        .add(PlayerMoveTrigger.class)
                    .targets()
                        .add(Player.class, PlayerTarget::new);
        }

        @Nested
        @DisplayName("with FlowParser")
        class FlowParser {

            @Test
            @DisplayName("should create list of nested actions")
            void shouldCreateListOfActions() throws ParseException {

                ArtContext context = scope.load(Arrays.asList(
                        "!damage 20",
                        "!dmg 50",
                        "!hit 10"
                ));

                Player player = new Player();
                player.setHealth(100);

                CombinedResult result = context.execute(player);

                assertThat(result.success()).isTrue();
                assertThat(player.getHealth()).isEqualTo(20);

                assertThat(result.ofTarget(new PlayerTarget(player)))
                        .hasSize(3)
                        .extracting(TargetResult::context)
                        .extracting(ArtObjectContext::meta)
                        .extracting(ArtObjectMeta::identifier)
                        .contains("damage", "damage", "damage");

            }

            @Test
            @DisplayName("should execute actions for multiple targets")
            void shouldCreateListOfActionsForMultipleTargets() throws ParseException {

                ArtContext context = scope.load(Arrays.asList(
                        "!damage 20",
                        "!dmg 50",
                        "!hit 10"
                ));

                Player[] players = new Player[10];
                for (int i = 0; i < 10; i++) {
                    Player player = new Player();
                    player.setHealth(100);
                    players[i] = player;
                }

                CombinedResult result = context.execute((Object[]) players);

                assertThat(result.success()).isTrue();
                assertThat(players).extracting(Player::getHealth)
                        .allMatch(integer -> integer == 20);

                assertThat(result.ofTarget(new PlayerTarget(players[5])))
                        .hasSize(3)
                        .extracting(TargetResult::context)
                        .extracting(ArtObjectContext::meta)
                        .extracting(ArtObjectMeta::identifier)
                        .contains("damage", "damage", "damage");

                assertThat(result.ofTarget(Player.class))
                        .hasSize(30);
            }

            @Test
            @DisplayName("should test requirements before executing actions")
            void shouldTestRequirementsBeforeAction() throws ParseException {

                ArtContext context = scope.load(Arrays.asList(
                        "?health >50",
                        "!damage 40"
                ));

                Player foo = new Player("foo");
                foo.setHealth(60);

                assertThat(context.execute(foo).success());
                assertThat(foo.getHealth()).isEqualTo(20);

                assertThat(context.execute(foo).failure());
                assertThat(foo.getHealth()).isEqualTo(20);
            }

            @Test
            @DisplayName("should test requirements of nested actions")
            void shouldTestRequirementsOfNestedActions() throws ParseException {

                ArtContext context = scope.load(Arrays.asList(
                        "?health >50",
                        "!damage 40",
                        "?health >50",
                        "!txt [awesome, you, did it]"
                ));

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
            void shouldExecuteActionsIfTriggerWasCalled() throws ParseException {

                Player player = new Player();
                player.setHealth(100);

                scope.load(Arrays.asList(
                        "@move",
                        "!damage 40"
                )).enableTrigger();

                scope.trigger(PlayerMoveTrigger.class).with(player).execute();

                assertThat(player.getHealth()).isEqualTo(60);
            }

            @Test
            @DisplayName("should execute trigger only once")
            void shouldOnlyExecuteTriggerOnce() throws ParseException {

                Player player = new Player();
                player.setHealth(100);

                scope.load(Arrays.asList(
                        "@move(execute_once=true)",
                        "!damage 40"
                )).enableTrigger();

                scope.trigger(PlayerMoveTrigger.class).with(player).execute();
                scope.trigger(PlayerMoveTrigger.class).with(player).execute();
                scope.trigger(PlayerMoveTrigger.class).with(player).execute();

                assertThat(player.getHealth()).isEqualTo(60);
            }

            @Test
            @DisplayName("should execute actions on both triggers")
            void shouldExecuteOneOrTheOtherTrigger() throws ParseException {

                Player player = new Player();
                player.setHealth(100);

                scope.load(Arrays.asList(
                        "@move",
                        "@damage",
                        "!damage 40"
                )).enableTrigger();

                scope.trigger(PlayerDamangeTrigger.class).with(player).execute();
                scope.trigger(PlayerMoveTrigger.class).with(player).execute();

                assertThat(player.getHealth()).isEqualTo(20);
            }

            @Test
            @DisplayName("should call the listener in the art context")
            void shouldCallTheListenerOfTheArtContext() throws ParseException {

                Player player = new Player();
                player.setHealth(100);

                ArtContext artContext = scope.load(Arrays.asList(
                        "@move"
                )).enableTrigger();

                TriggerListener<Player> listener = spy(new TriggerListener<Player>() {
                    @Override
                    public void onTrigger(Target<Player> targets, ExecutionContext<TriggerContext> context) {

                    }
                });

                artContext.onTrigger(Player.class, listener);

                scope.trigger(PlayerMoveTrigger.class).with(player).execute();

                verify(listener, times(1)).onTrigger(any(), any());
            }
        }
    }
}
