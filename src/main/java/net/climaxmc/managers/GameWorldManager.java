package net.climaxmc.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.File;

public class GameWorldManager extends Manager {
    private File mapsFolder;

    GameWorldManager() {
        initializeWorldFolders();
    }

    private void initializeWorldFolders() {
        File root = new File((System.getProperty("os.name").startsWith("Windows") ? "C:" : "") + File.separator + "maps");
        mapsFolder = new File(root.getPath() + File.separator + manager.getGame().getName());

        if (mapsFolder.mkdirs()) {
            plugin.getLogger().info("Created maps folder at " + mapsFolder.getPath());
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}
