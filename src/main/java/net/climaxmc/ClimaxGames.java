package net.climaxmc;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "ClimaxGames", name = "ClimaxGames", version = "1.0")
public class ClimaxGames {
    @Inject
    private Logger logger;
    @Inject
    private Game game;

    @Subscribe
    public void onServerStart(ServerStartingEvent event) {

    }

    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {

    }

    /**
     * Gets the logger
     *
     * @return Logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets the game (not to be confused with Minigames)
     *
     * @return Game
     */
    public Game getGame() {
        return game;
    }
}
