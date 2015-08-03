package net.climaxmc;

import net.climaxmc.managers.GameManager;
import net.climaxmc.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;

public class ClimaxGames extends JavaPlugin {
    private static ClimaxGames instance;

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

        manager = new GameManager();
        updater = new Updater(this);
    }

    @Override
    public void onDisable() {

    }

    /**
     * Gets the game manager
     *
     * @return Game manager
     */
    public GameManager getManager() {
        return manager;
    }
}
