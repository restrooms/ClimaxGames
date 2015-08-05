package net.climaxmc.game;

import lombok.*;
import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.WorldConfig;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.*;

/**
 * Represents a game
 */
@Getter
public abstract class Game implements Listener {
    @Getter(value = AccessLevel.NONE)
    protected static ClimaxGames plugin = ClimaxGames.getInstance();
    private String name;
    private Kit[] kits;
    private GameState state = GameState.READY;
    @Setter
    private WorldConfig worldConfig = new WorldConfig("None", "None", new HashMap<>());

    private List<Location> lobbyKitEntityLocations = Arrays.asList(
            new Location(plugin.getServer().getWorld("world"), 88.5, 69, 86.5, -45, 0),
            new Location(plugin.getServer().getWorld("world"), 96.5, 69, 86.5, 45, 0),
            new Location(plugin.getServer().getWorld("world"), 96.5, 69, 94.5, 135, 0),
            new Location(plugin.getServer().getWorld("world"), 88.5, 69, 94.5, -135, 0)
    );

    private List<GameTeam> teams = new ArrayList<>();

    // Game specific variables
    protected int minPlayers = 1; //TODO CHANGE TO 4, ONLY 2 FOR DEBUGGING
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
     * Attempt to start the countdown
     */
    public void startCountdown() {
        if (!GameCountdown.isStarted()) {
            new GameCountdown().runTaskTimer(plugin, 0, 20);
        }
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
        return state.equals(GameState.IN_GAME);
    }

    /**
     * Possible states of games
     */
    public enum GameState {
        READY,
        PREPARE,
        IN_GAME;
    }
}
