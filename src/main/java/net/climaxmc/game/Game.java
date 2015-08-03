package net.climaxmc.game;

import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.kit.Kit;

/**
 * Represents a game
 */
public abstract class Game {
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
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the name of the game
     *
     * @return Name of game
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the kits of the game
     *
     * @return Kits of game
     */
    public Kit[] getKits() {
        return kits;
    }

    /**
     * Gets the state of the game
     *
     * @return State of game
     */
    public GameState getState() {
        return state;
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
