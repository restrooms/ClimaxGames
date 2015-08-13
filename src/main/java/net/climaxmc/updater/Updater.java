package net.climaxmc.updater;

import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.utilities.F;
import net.climaxmc.utilities.UtilPlugin;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Updater implements Listener {
    private ClimaxGames plugin;

    public Updater(ClimaxGames plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onUpdateAvailable(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.READY)) {
            return;
        }

        boolean windows = System.getProperty("os.name").startsWith("Windows");
        File file = new File((windows ? "C:" : File.separator + "tmp") + File.separator + "update" + File.separator + "ClimaxGames.jar");
        File oldFile = new File("plugins" + File.separator + "ClimaxGames.jar");

        if (!file.exists() || !oldFile.exists()) {
            return;
        }

        try {
            if (DigestUtils.md5Hex(new FileInputStream(file)).equals(DigestUtils.md5Hex(new FileInputStream(oldFile)))) {
                return;
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not compare update to original! " + e.getMessage());
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getServer().broadcastMessage(F.message("Updater", "Reloading in 5 seconds for an update..."));

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                UtilPlugin.unload(plugin);

                try {
                    Files.copy(file.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not copy update! " + e.getMessage());
                }

                UtilPlugin.load("ClimaxGames");

                plugin.getServer().broadcastMessage(F.message("Updater", "Reload complete!"));
            }, 100);
        }, 40);
    }
}
