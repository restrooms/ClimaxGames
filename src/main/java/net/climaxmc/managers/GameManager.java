package net.climaxmc.managers;

import com.google.common.collect.Sets;
import net.climaxmc.ClimaxGames;

import java.util.Set;

public class GameManager implements Manager {
    private ClimaxGames plugin;
    private Set<Manager> managers;

    public GameManager(ClimaxGames plugin) {
        this.plugin = plugin;

        managers = Sets.newHashSet(this,
                new GameCommandManager(this),
                new GameCreationManager(this),
                new GameLobbyManager(this),
                new GamePlayerManager(this),
                new GameWorldManager(this)
        );

        managers.forEach(manager -> plugin.getGame().getEventManager().register(plugin, manager));
    }

    protected ClimaxGames getPlugin() {
        return plugin;
    }
}
