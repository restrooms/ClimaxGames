package net.climaxmc.game;

import lombok.Data;
import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.kit.Kit;
import org.bukkit.event.Listener;

/**
 * Represents a game
 */
@Data
public abstract class Game implements Listener {
    protected static ClimaxGames plugin = ClimaxGames.getInstance();
    private String name;
    private Kit[] kits;
    private GameState state;

    /**
     * Defines a game
     *
     * @param name Name of game
     * @param kits Kits of game
     */
    public Game(String name, Kit[] kits) {
        this.name = name;
        this.kits = kits;
        this.state = GameState.READY;

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
        STARTING,
        IN_GAME,
        ENDING,
        RESETTING;
    }
}
