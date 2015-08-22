package net.climaxmc.game;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.*;
import net.climaxmc.ClimaxGames;
import net.climaxmc.core.ClimaxCore;
import net.climaxmc.core.mysql.GameType;
import net.climaxmc.core.mysql.PlayerData;
import net.climaxmc.core.utilities.C;
import net.climaxmc.core.utilities.F;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.kit.Kit;
import org.bukkit.Sound;
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
    protected boolean naturalRegeneration = true;
    protected List<String> places = new ArrayList<>();

    private String name;
    private GameType type;
    private Kit[] kits;
    private GameState state = GameState.READY;
    @Setter
    private WorldConfig worldConfig = new WorldConfig("None", "None", new ArrayList<>());
    private Map<UUID, Kit> playerKits = new HashMap<>();
    private Map<UUID, Multimap<String, Integer>> coinEarnings = new HashMap<>();

    /**
     * Defines a game
     *
     * @param name Name of game
     * @param kits Kits of game
     */
    public Game(String name, GameType type, Kit[] kits) {
        this.name = name;
        this.type = type;
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
        for (String place : places) {
            if (i == 3) {
                return;
            }

            plugin.getServer().broadcastMessage(C.BOLD + "#" + ++i + " " + C.BLUE + place);
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

    @EventHandler
    public void onGameAboutToEnd(GameStateChangeEvent event) {
        if (!event.getState().equals(GameState.END)) {
            return;
        }

        announceEnd(places);
        plugin.getServer().getScheduler().runTaskLater(plugin, this::giveCoins, 40);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> setState(GameState.READY), 120);
        plugin.getServer().getScheduler().cancelTask(endCheckTask.getTaskId());
        worldConfig.getTeams().clear();
    }

    private void giveCoins() {
        if (coinEarnings.size() == 0) {
            return;
        }
        coinEarnings.forEach((playerUUID, coinEarning) -> {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) {
                return;
            }
            PlayerData playerData = ClimaxCore.getPlayerData(player);
            player.sendMessage(F.topLine());
            player.sendMessage(C.RED + C.BOLD + "C" + C.GOLD + C.BOLD + "Coins:");
            for (String reason : coinEarning.keys()) {
                for (Integer amount : coinEarning.get(reason)) {
                    player.sendMessage(C.GRAY + reason + " " + C.STRIKETHROUGH + "--" + C.GOLD + " " + amount + " " + C.RED + "C" + C.GOLD + "Coins");
                    playerData.depositCoins(amount);
                }
            }
            player.sendMessage(F.bottomLine());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 2);
        });
        coinEarnings.clear();
    }

    public void addCoins(Player player, String reason, int amount) {
        if (!coinEarnings.containsKey(player.getUniqueId())) {
            coinEarnings.put(player.getUniqueId(), HashMultimap.<String, Integer>create());
        }

        coinEarnings.get(player.getUniqueId()).put(reason, amount);
    }

    @EventHandler
    public void onGameReady(GameStateChangeEvent event) {
        if (!event.getState().equals(GameState.READY)) {
            return;
        }

        startCountdown();
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
        public TeamGame(String name, GameType type, Kit[] kits) {
            super(name, type, kits);
        }

        @Override
        public void checkEnd() {
            if (!hasStarted()) {
                return;
            }

            List<String> teamsAlive = new ArrayList<>();
            getWorldConfig().getTeams().stream().filter(team -> team.getPlayers(false).size() > 0).forEach(team -> teamsAlive.add(team.getColorCode() + C.BOLD + team.getName()));
            if (teamsAlive.size() <= 1) {
                if (teamsAlive.size() > 0) {
                    places = teamsAlive;
                }
                setState(GameState.END);
            }
        }
    }
}
