package net.climaxmc.game;

import lombok.*;
import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
    protected int minPlayers = 1; //TODO CHANGE TO 4, ONLY 2 FOR DEBUGGING
    protected int maxPlayers = 16;
    protected boolean respawnOnDeath = false;
    protected boolean fallDamage = false;
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
        if (!GameCountdown.isStarted()) {
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
        return worldConfig.getTeams().stream().filter(team -> team.getPlayers().contains(player.getUniqueId())).findFirst().get();
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
        plugin.getServer().broadcastMessage(C.BLUE + C.BOLD + getWorldConfig().getMapName() + C.GRAY + " \u00bb by " + C.WHITE + C.BOLD + "Arcane Builds");
        plugin.getServer().broadcastMessage("");
        int i = 0;
        for (String playerName : places) {
            if (i == 3) {
                return;
            }

            plugin.getServer().broadcastMessage(C.BOLD + "#" + ++i + " " + C.BLUE + playerName);
        }
        plugin.getServer().broadcastMessage("");
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

        //TODO DEBUG endCheckTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkEnd, 0, 5);
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

    public abstract static class FreeForAll extends Game {

        /**
         * Defines a free for all game
         *
         * @param name Name of game
         * @param kits Kits of game
         */
        public FreeForAll(String name, Kit[] kits) {
            super(name, kits);
        }

        @Override
        public void checkEnd() {
            if (UtilPlayer.getAll(false).size() <= 1) {
                UtilPlayer.getAll().forEach(player -> player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 1.0f));
            }
            setState(GameState.END);
        }

        @EventHandler
        public void onPlayerDeathAddPlace(PlayerDeathEvent event) {
            places.add(0, event.getEntity().getName());
        }
    }

    @EventHandler
    public void onGameAboutToEnd(GameStateChangeEvent event) {
        if (!event.getState().equals(GameState.END)) {
            return;
        }

        announceEnd(places);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> setState(GameState.READY), 120L);
        if (endCheckTask != null) { //TODO REMOVE DUE TO DEBUG
            plugin.getServer().getScheduler().cancelTask(endCheckTask.getTaskId());
        }
    }
}
