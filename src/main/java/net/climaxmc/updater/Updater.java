package net.climaxmc.updater;

import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class Updater implements Listener {
    private ClimaxGames plugin;

    public Updater(ClimaxGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onUpdateAvailable(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.READY)) {
            return;
        }

        boolean windows = System.getProperty("os.name").startsWith("Windows");
        File file = new File((windows ? "C:" : File.separator + "tmp") + File.separator + "update");

        if (!file.exists()) {
            return;
        }


    }
}
