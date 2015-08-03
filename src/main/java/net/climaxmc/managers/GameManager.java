package net.climaxmc.managers;

import com.google.common.collect.Sets;
import net.climaxmc.game.Game;

import java.util.Set;

public class GameManager extends Manager {
    private Set<Manager> managers;
    private Game game = null;

    public GameManager() {
        manager = this;

        managers = Sets.newHashSet(this,
                new GameCommandManager(),
                new GameConfigurationManager(),
                new GameCreationManager(),
                new GameLobbyManager(),
                new GamePlayerManager(),
                new GameWorldManager()
        );

        managers.forEach(manager -> plugin.getServer().getPluginManager().registerEvents(manager, plugin));
    }

    /**
     * Gets the current game
     *
     * @return Current game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Set the current game
     *
     * @param game Game to set to
     */
    public void setGame(Game game) {
        this.game = game;
    }
}
