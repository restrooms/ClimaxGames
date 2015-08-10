package net.climaxmc;

import lombok.Getter;
import net.climaxmc.managers.GameManager;
import net.climaxmc.mysql.MySQL;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.updater.Updater;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ClimaxGames extends JavaPlugin {
    private static ClimaxGames instance;

    @Getter
    private MySQL mySQL;
    private GameManager manager;
    private Updater updater;

    /**
     * Get the plugin instance
     *
     * @return Plugin instance
     */
    public static ClimaxGames getInstance() {
        return instance;
    }

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
     * Gets the game manager
     *
     * @return Game manager
     */
    public GameManager getManager() {
        return manager;
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID playerUUID) {
        return mySQL.getPlayerData(playerUUID);
    }
}
