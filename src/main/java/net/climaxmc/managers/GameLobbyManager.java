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
        initializeKitSelectorEntities();

        UtilPlayer.getAll().forEach(player -> {
            manager.getGame().getPlayerKits().put(player.getUniqueId(), manager.getGame().getKits()[0]);
        });
    }

    private void initializeKitSelectorEntities() {
        plugin.getServer().getWorld("world").getEntities().stream().filter(entity -> entity.getType().equals(EntityType.VILLAGER) || entity.getType().equals(EntityType.ARMOR_STAND)).forEach(Entity::remove);

        int i = 0;
        for (Kit kit : manager.getGame().getKits()) {
            if (manager.getLobbyKitEntityLocations().size() >= i) {
                kit.spawnEntity(manager.getLobbyKitEntityLocations().get(++i));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameFinished(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.READY)) {
            return;
        }

        UtilPlayer.getAll().forEach(player -> {
            player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
            manager.initializeLobbyScoreboard(player);
        });
    }
}
