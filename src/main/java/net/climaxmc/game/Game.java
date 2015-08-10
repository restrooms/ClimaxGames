package net.climaxmc.game;

import lombok.*;
import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Represents a game
 */
@Getter
public abstract class Game implements Listener {
    @Getter(value = AccessLevel.NONE)
    protected static ClimaxGames plugin = ClimaxGames.getInstance();
    private static BukkitTask endCheckTask = null;

    // Game specific variables
    protected int minPlayers = 2; //TODO CHANGE TO 4, ONLY 2 FOR DEBUGGING
    protected int maxPlayers = 16;
    protected boolean respawnOnDeath = false;
    protected boolean fallDamage = false;
    protected boolean cancelInteract = true;
    protected List<String> places = new ArrayList<>();

    private String name;
    private Kit[] kits;
    private GameState state = GameState.READY;
    @Setter
    private WorldConfig worldConfig = new WorldConfig("None", "None", new ArrayList<>());
    private HashMap<UUID, Kit> playerKits = new HashMap<>();

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
        if (!GameCountdown.isStarted() && state.equals(GameState.READY)) {
            new GameCountdown().runTaskTimer(plugin, 0, 20);
        }
    }

    public abstract void checkEnd();

    /**
     * Gets the team of a player
     *
     * @param player Player to get team of
     * @return Player team
     */
    public GameTeam getPlayerTeam(Player player) {
        Optional<GameTeam> playerTeamOptional = worldConfig.getTeams().stream().filter(team -> team.getPlayers().contains(player.getUniqueId())).findFirst();
        GameTeam playerTeam;
        if (!playerTeamOptional.isPresent()) {
            GameTeam smallest = worldConfig.getTeams().get(0);
            final List<UUID> smallestPlayers = smallest.getPlayers();
            Optional<GameTeam> smallestOptional = worldConfig.getTeams().stream().filter(team -> team.getPlayers().size() < smallestPlayers.size()).findFirst();
            if (smallestOptional.isPresent()) {
                smallest = smallestOptional.get();
            }
            smallest.getPlayers().add(player.getUniqueId());
            playerTeam = smallest;
        } else {
            playerTeam = playerTeamOptional.get();
        }
        return playerTeam;
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

    public void announceEnd(List<String> places) {
        plugin.getServer().broadcastMessage(F.topLine());
        int i = 0;
        for (String playerName : places) {
            if (i == 3) {
                return;
            }

            plugin.getServer().broadcastMessage(C.BOLD + "#" + ++i + " " + C.BLUE + playerName);
        }
        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage(C.BLUE + C.BOLD + worldConfig.getMapName() + C.GRAY + " \u00bb by " + C.WHITE + C.BOLD + "Arcane Builds");
        plugin.getServer().broadcastMessage(F.bottomLine());
    }

    /**
     * Gets if the game has started yet
     *
     * @return If the game has started yet
     */
    public boolean hasStarted() {
        return state.equals(GameState.IN_GAME);
    }

    @EventHandler
    public void onGameStart(GameStateChangeEvent event) {
        if (!event.getState().equals(GameState.IN_GAME)) {
            return;
        }

        endCheckTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkEnd, 0, 5);
    }

    /**
     * Possible states of games
     */
    public enum GameState {
        READY,
        PREPARE,
        IN_GAME,
        END
    }

    public abstract static class TeamGame extends Game {
        /**
         * Defines a team game
         *
         * @param name Name of game
         * @param kits Kits of game
         */
        public TeamGame(String name, Kit[] kits) {
            super(name, kits);
        }

        @Override
        public void checkEnd() {
            ArrayList<String> teamsAlive = new ArrayList<>();
            getWorldConfig().getTeams().stream().filter(team -> team.getPlayers(false).size() > 0).forEach(team -> teamsAlive.add(team.getColorCode() + C.BOLD + team.getName()));
            if (teamsAlive.size() <= 1) {
                if (teamsAlive.size() > 0) {
                    places = teamsAlive;
                }
                setState(GameState.END);
            }
        }
    }

    @EventHandler
    public void onGameAboutToEnd(GameStateChangeEvent event) {
        if (!event.getState().equals(GameState.END)) {
            return;
        }

        announceEnd(places);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> setState(GameState.READY), 120L);
        plugin.getServer().getScheduler().cancelTask(endCheckTask.getTaskId());
    }
}
