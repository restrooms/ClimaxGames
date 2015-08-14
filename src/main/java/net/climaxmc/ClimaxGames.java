package net.climaxmc;

import lombok.Getter;
import net.climaxmc.managers.GameManager;
import net.climaxmc.mysql.MySQL;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.updater.Updater;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ClimaxGames extends JavaPlugin {
    @Getter
    private static ClimaxGames instance;

    @Getter
    private MySQL mySQL;
    @Getter
    private GameManager manager;

    private Set<PlayerData> cachedPlayerData = new HashSet<>();
    @Getter
    private Map<UUID, Long> playerOnTimes = new HashMap<>();
    @Getter
    private int serverID;

    @Override
    public void onEnable() {
        instance = this;

        mySQL = new MySQL(this, "localhost", 3306, "climax", "plugin", "rR6nCbqaFTPCZqHA");
        manager = new GameManager();
        new Updater(this);

        serverID = mySQL.createServer(manager.getGame().getType());
    }

    @Override
    public void onDisable() {
        mySQL.deleteServer(serverID);
        mySQL.closeConnection();
    }

    /**
     * Gets the data of a player
     *
     * @param player Player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    /**
     * Gets the data of a player
     *
     * @param playerUUID UUID of the player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(UUID playerUUID) {
        for (PlayerData playerData : cachedPlayerData) {
            if (playerData != null && playerData.getUuid() != null && playerData.getUuid().equals(playerUUID)) {
                return playerData;
            }
        }
        PlayerData playerData = mySQL.getPlayerData(playerUUID);
        cachedPlayerData.add(playerData);
        return playerData;
    }

    /**
     * Gets the data of a player
     *
     * @param playerName Name of the player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(String playerName) {
        for (PlayerData playerData : cachedPlayerData) {
            if (playerData != null && playerData.getName() != null && playerData.getName().equalsIgnoreCase(playerName)) {
                return playerData;
            }
        }
        PlayerData playerData = mySQL.getPlayerData(playerName);
        cachedPlayerData.add(playerData);
        return playerData;
    }

    /**
     * Gets the data of a player
     *
     * @param playerID ID of the player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(int playerID) {
        for (PlayerData playerData : cachedPlayerData) {
            if (playerData != null && playerData.getId() == playerID) {
                return playerData;
            }
        }
        PlayerData playerData = mySQL.getPlayerData(playerID);
        cachedPlayerData.add(playerData);
        return playerData;
    }

    /**
     * Clears the data of a player
     *
     * @param playerData Player data to clear
     */
    public void clearCache(PlayerData playerData) {
        cachedPlayerData.remove(playerData);
    }
}
