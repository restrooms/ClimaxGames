package net.climaxmc.managers;

import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.game.GameTeam;
import net.climaxmc.utilities.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameWorldManager extends Manager {
    private File mapsFolder;

    GameWorldManager() {
        initializeMapFolders();
    }

    private void initializeMapFolders() {
        File root = new File((System.getProperty("os.name").startsWith("Windows") ? "C:" : "") + File.separator + "maps");
        mapsFolder = new File(root.getPath() + File.separator + manager.getGame().getName());

        // Create root map directories
        if (mapsFolder.mkdirs()) {
            plugin.getLogger().info("Created maps folder at " + mapsFolder.getPath());
        }
    }

    private void loadMapFolders() {
        File[] mapFiles = mapsFolder.listFiles();

        // Check if there are no maps
        if (mapFiles == null || mapFiles.length <= 0) {
            plugin.getLogger().severe("No maps available to load!");
            return;
        }

        // Check and delete existing map folder
        File gameMapFolder = new File(manager.getGame().getName());
        if (gameMapFolder.exists()) {
            try {
                FileUtils.deleteDirectory(gameMapFolder);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not delete existing map!");
            }
        }

        // Extract world ZIP file
        int mapId = RandomUtils.nextInt(mapFiles.length);
        int i = 0;
        for (File mapZipFile : mapFiles) {
            if (!mapZipFile.getName().endsWith(".zip")) {
                continue;
            }

            if (i++ == mapId) {
                try {
                    new ZipFile(mapZipFile).extractAll(manager.getGame().getName());
                } catch (ZipException e) {
                    plugin.getLogger().severe("Could not extract zip file " + mapZipFile.getPath());
                }

                plugin.getLogger().info("Created " + manager.getGame().getName() + " world directory");
            }
        }
    }

    private void loadWorldConfig() {
        // Get world configuration
        File worldConfigFile = new File(new File(manager.getGame().getName()).getPath() + File.separator + "WorldConfig.yml");
        if (!worldConfigFile.exists()) {
            plugin.getLogger().severe("World configuration does not exist!");
            return;
        }
        FileConfiguration worldConfig = YamlConfiguration.loadConfiguration(worldConfigFile);
        List<GameTeam> teams = new ArrayList<>();
        for (String team : worldConfig.getConfigurationSection("Spawns").getKeys(false)) {
            List<Location> spawns = new ArrayList<>();
            for (String locationKey : worldConfig.getConfigurationSection("Spawns." + team).getKeys(false)) {
                String path = "Spawns." + team + "." + locationKey + ".";
                World world = plugin.getServer().getWorld(manager.getGame().getName());
                double x = worldConfig.getDouble(path + "X");
                double y = worldConfig.getDouble(path + "Y");
                double z = worldConfig.getDouble(path + "Z");
                float yaw = (float) worldConfig.getDouble(path + "Yaw");
                float pitch = (float) worldConfig.getDouble(path + "Pitch");
                spawns.add(new Location(world, x, y, z, yaw, pitch));
            }
            String teamColorCode = C.getColor(team);
            teams.add(new GameTeam(team, spawns, new ArrayList<>(), C.getColorFromCode(teamColorCode), teamColorCode));
        }
        manager.getGame().setWorldConfig(new WorldConfig(worldConfig.getString("Name"), worldConfig.getString("Author"), teams));

        UtilPlayer.getAll().forEach(manager::initializeLobbyScoreboard);
    }

    @EventHandler
    public void onWorldReadyToReload(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.READY)) {
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            World gameWorld = plugin.getServer().getWorld(manager.getGame().getName());

            if (gameWorld != null) {
                plugin.getServer().unloadWorld(gameWorld, false);
            }

            loadMapFolders();

            World world = plugin.getServer().createWorld(new WorldCreator(manager.getGame().getName()).generatorSettings("1;0;1").type(WorldType.FLAT));
            world.setGameRuleValue("doMobSpawning", "false");

            loadWorldConfig();
        });
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}
