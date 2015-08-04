package net.climaxmc.managers;

import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.UtilPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class GameLobbyManager extends Manager {
    GameLobbyManager() {
        initializeKitSelector();
    }

    private void initializeKitSelector() {
        plugin.getServer().getWorld("world").getEntities().stream().filter(entity -> entity.getType().equals(EntityType.ZOMBIE)).forEach(Entity::remove);

        int i = 0;
        for (Kit kit : manager.getGame().getKits()) {
            kit.spawnEntity(plugin.getServer().getWorld("world").getSpawnLocation().add(0, 0, i++));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameFinished(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.READY)) {
            return;
        }

        UtilPlayer.getAll().forEach(player -> player.teleport(plugin.getServer().getWorld("world").getSpawnLocation()));
    }
}
