package net.climaxmc.managers;

import com.google.common.collect.Sets;
import lombok.*;
import net.climaxmc.game.Game;
import org.bukkit.Location;

import java.util.*;

public class GameManager extends Manager {
    private Set<Manager> managers;
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private Game game = null;

    @Getter
    private List<Location> lobbyKitEntityLocations = Arrays.asList(
            new Location(plugin.getServer().getWorld("world"), 88.5, 69, 86.5, -45, 0),
            new Location(plugin.getServer().getWorld("world"), 96.5, 69, 86.5, 45, 0),
            new Location(plugin.getServer().getWorld("world"), 96.5, 69, 94.5, 135, 0),
            new Location(plugin.getServer().getWorld("world"), 88.5, 69, 94.5, -135, 0)
    );

    public GameManager() {
        manager = this;

        managers = Sets.newHashSet(this,
                new GameConfigurationManager(),
                new GameWorldManager(),
                new GameLobbyManager(),
                new GameCommandManager(),
                new GamePlayerManager()
        );

        managers.forEach(manager -> plugin.getServer().getPluginManager().registerEvents(manager, plugin));

        game.setState(Game.GameState.READY);
    }
}
