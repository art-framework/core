package io.artframework.bukkit.actions;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.artframework.ActionContext;
import io.artframework.ExecutionContext;
import io.artframework.bukkit.targets.PlayerTarget;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GiveItemActionTest {

    protected ServerMock server;
    private GiveItemAction action;
    private ExecutionContext<ActionContext<Player>> context;
    private PlayerMock player;

    @BeforeEach
    void setUp() {


        server = MockBukkit.mock();
        action = new GiveItemAction()
                .item(Material.DIAMOND)
                .amount(100);
        context = mock(ExecutionContext.class);
        player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Test
    @DisplayName("should give items to the player")
    void shouldGiveItemsToPlayer() {

        assertThat(action.execute(new PlayerTarget(player), context).success())
                .isTrue();

        assertThat(player.getInventory().contains(Material.DIAMOND, 100)).isTrue();
    }
}