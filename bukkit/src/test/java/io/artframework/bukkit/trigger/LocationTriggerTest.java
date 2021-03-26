package io.artframework.bukkit.trigger;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.artframework.*;
import io.artframework.bukkit.targets.BukkitEventTarget;
import io.artframework.bukkit.targets.EntityTarget;
import io.artframework.bukkit.targets.PlayerTarget;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.*;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

class LocationTriggerTest {

    private static ServerMock server;
    private static LocationTrigger trigger;

    @BeforeAll
    static void beforeAll() {
        server = MockBukkit.mock();
        trigger = new LocationTrigger(ART.scope());

        ART.scope().register()
                .targets().add(Entity.class, EntityTarget::new)
                .add(Event.class, BukkitEventTarget::new)
                .and()
                .trigger().add(LocationTrigger.class, () -> new LocationTrigger(ART.scope()));
    }

    @AfterAll
    static void afterAll() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("should parse location trigger into art context")
    void shouldParseLocationTrigger() {

        assertThatCode(() -> ART.load(Collections.singletonList(
                "@loc 100,50,-100"
        ))).doesNotThrowAnyException();
    }

    @Test
    @Disabled(value = "only fails if all test suites are executed at the same time. needs debugging.")
    @SneakyThrows
    @DisplayName("should call trigger when player moves to location")
    void shouldCallTriggerWhenPlayerMovesToLocation() {

        ArtContext context = ART.load(Collections.singletonList(
                "@loc 100,50,-100"
        ));

        TriggerListener<Player> listener = spy(new TriggerListener<>() {
            @Override
            public void onTrigger(Target<Player> target, ExecutionContext<TriggerContext> context) {

            }
        });
        context.onTrigger(Player.class, listener);
        context.enableTrigger();

        PlayerMock player = server.addPlayer();
        Location from = new Location(player.getWorld(), 0, 0, 0);
        Location to = new Location(player.getWorld(), 100, 50, -100);
        player.setLocation(from);
        trigger.onMove(new PlayerMoveEvent(player, from, from));
        player.setLocation(to);
        trigger.onMove(new PlayerMoveEvent(player, from, to));

        verify(listener, times(1)).onTrigger(eq(new PlayerTarget(player)), any());
    }

    @SneakyThrows
    @Test
    @DisplayName("should not call listener if location does not match configured location")
    void shouldNotCallListenerIfLocationDoesNotMatchConfiguredLocation() {

        ArtContext context = ART.load(Collections.singletonList(
                "@loc 100,50,-100"
        ));

        TriggerListener<Player> listener = spy(new TriggerListener<>() {
            @Override
            public void onTrigger(Target<Player> target, ExecutionContext<TriggerContext> context) {

            }
        });
        context.onTrigger(Player.class, listener);
        context.enableTrigger();

        PlayerMock player = server.addPlayer();
        Location to = new Location(player.getWorld(), 0, 0, 0);
        Location from = new Location(player.getWorld(), 100, 50, -100);
        trigger.onMove(new PlayerMoveEvent(player, from, from));
        trigger.onMove(new PlayerMoveEvent(player, from, to));

        verify(listener, never()).onTrigger(any(), any());
    }
}