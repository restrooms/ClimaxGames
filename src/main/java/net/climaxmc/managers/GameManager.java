package net.climaxmc.managers;

import com.google.common.collect.Sets;
import lombok.*;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.utilities.UtilPlayer;
import org.bukkit.event.EventHandler;

import java.util.Set;

public class GameManager extends Manager {
    private Set<Manager> managers;
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private Game game = null;

    public GameManager() {
        manager = this;

        managers = Sets.newHashSet(this,
                new GameCommandManager(),
                new GameConfigurationManager(),
                new GameLobbyManager(),
                new GamePlayerManager(),
                new GameWorldManager()
        );

        managers.forEach(manager -> plugin.getServer().getPluginManager().registerEvents(manager, plugin));

    }

    @EventHandler
    public void onGameReadyForTeleport(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.IN_GAME)) {
            return;
        }

        UtilPlayer.getAll().forEach(player -> player.teleport(plugin.getServer().getWorld(game.getName()).getSpawnLocation()));
    }
}
