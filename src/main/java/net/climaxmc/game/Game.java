package net.climaxmc.game;

import lombok.Getter;
import lombok.Setter;
import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.WorldConfig;
import org.bukkit.event.Listener;

import java.util.HashMap;

/**
 * Represents a game
 */
public abstract class Game implements Listener {
    protected static ClimaxGames plugin = ClimaxGames.getInstance();
    @Getter
    private String name;
    @Getter
    private Kit[] kits;
    @Getter
    private GameState state = GameState.READY;
    @Getter
    @Setter
    private WorldConfig worldConfig = new WorldConfig("None", "None", new HashMap<>());

    // Game specific variables
    protected int minPlayers = 4;
    protected int maxPlayers = 16;

    /**
     * Defines a game
     *
     * @param name Name of game
     * @param kits Kits of game
     */
    public Game(String name, Kit[] kits) {
        this.name = name;
        this.kits = kits;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Sets the state of the game
     *
     * @param state State to set game to
     */
    public void setState(GameState state) {
        this.state = state;
        plugin.getServer().getPluginManager().callEvent(new GameStateChangeEvent(this, state));
        plugin.getLogger().info(name + " state set to " + state.toString());
    }

    /**
     * Gets if the game has started yet
     *
     * @return If the game has started yet
     */
    public boolean hasStarted() {
        return getState().equals(GameState.IN_GAME);
    }

    /**
     * Possible states of games
     */
    public enum GameState {
        READY,
        PREPARE,
        IN_GAME,
        CLEANUP;
    }
}
