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
    private Updater updater;

    private Map<UUID, PlayerData> cachedPlayerData = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        mySQL = new MySQL(this, "localhost", 3306, "climax", "plugin", "rR6nCbqaFTPCZqHA");
        manager = new GameManager();
        updater = new Updater(this);
    }

    @Override
    public void onDisable() {
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
        if (cachedPlayerData.containsKey(playerUUID)) {
            return cachedPlayerData.get(playerUUID);
        }
        return mySQL.getPlayerData(playerUUID);
    }
}
