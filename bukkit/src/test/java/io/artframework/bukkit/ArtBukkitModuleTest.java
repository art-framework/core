package io.artframework.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.artframework.ART;
import io.artframework.ParseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Nested
    class CommandParser {

        Command command;

        @BeforeEach
        void setUp() {

            command = spy(new Command("foobar", null, null, Arrays.asList("foobar")) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {

                    server.getLogger().info("executed: /" + commandLabel + " " + String.join(" ", args));
                    return true;
                }
            });
            server.getCommandMap().register("foo", "foobar", command);
        }

        @Test
        @DisplayName("should execute console commands with params")
        void shouldExecuteConsoleCommandWithParams() throws ParseException {

            PlayerMock player = server.addPlayer("bar");

            ART.load(Collections.singletonList(
                    "/foobar:foo(execute_once:true) ${player} diamond"
            )).execute(player);

            verify(command, times(1))
                    .execute(server.getConsoleSender(), "foobar:foo", new String[]{"bar", "diamond"});
        }

        @Test
        @DisplayName("should execute console commands with colons")
        void shouldExecuteConsoleCommandWithColons() throws ParseException {

            PlayerMock player = server.addPlayer("bar");

            ART.load(Collections.singletonList(
                    "/foobar:foo ${player} diamond"
            )).execute(player);

            verify(command, times(1))
                    .execute(server.getConsoleSender(), "foobar:foo", new String[]{"bar", "diamond"});
        }
    }
}