package io.artframework.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.artframework.ART;
import io.artframework.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class ArtBukkitModuleTest {

    private static ServerMock server;
    private static ArtBukkitPlugin plugin;

    @BeforeAll
    static void beforeAll() {

        server = MockBukkit.mock();
        plugin = MockBukkit.load(ArtBukkitPlugin.class);
        server.getScheduler().performOneTick();
    }

    @AfterAll
    static void afterAll() {

        MockBukkit.unmock();
    }

    @Test
    @DisplayName("should replace player name")
    void shouldReplacePlayerName() throws ParseException {

        PlayerMock player = server.addPlayer();

        ART.load(Collections.singletonList(
                "!txt ${player}"
        )).execute(player);

        player.assertSaid(player.getName());
    }
}