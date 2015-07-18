package net.climaxmc.updater;

import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import org.spongepowered.api.event.Subscribe;

import java.io.File;

public class Updater {
    private ClimaxGames plugin;

    public Updater(ClimaxGames plugin) {
        this.plugin = plugin;
    }

    @Subscribe
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
