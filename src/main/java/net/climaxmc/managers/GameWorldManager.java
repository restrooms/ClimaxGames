package net.climaxmc.managers;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.File;

public class GameWorldManager extends Manager {
    private File mapsFolder;

    GameWorldManager() {
        initializeMapFolders();
        loadMaps();
    }

    private void initializeMapFolders() {
        File root = new File((System.getProperty("os.name").startsWith("Windows") ? "C:" : "") + File.separator + "maps");
        mapsFolder = new File(root.getPath() + File.separator + manager.getGame().getName());

        if (mapsFolder.mkdirs()) {
            plugin.getLogger().info("Created maps folder at " + mapsFolder.getPath());
        }
    }

    private void loadMaps() {
        File[] mapFiles = mapsFolder.listFiles();

        if (mapFiles == null || mapFiles.length <= 0) {
            plugin.getLogger().severe("No maps available to load!");
            return;
        }

        int mapId = RandomUtils.nextInt(mapFiles.length);
        int i = 0;
        for (File mapZipFile : mapFiles) {
            if (!mapZipFile.getName().endsWith(".zip")) {
                continue;
            }

            if (i++ == mapId) {
                try {
                    new ZipFile(mapZipFile).extractAll("GameMap");
                } catch (ZipException e) {
                    plugin.getLogger().severe("Could not extract zip file " + mapZipFile.getPath());
                }
            }
        }

    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}
