package net.climaxmc;

import com.google.inject.Inject;
import net.climaxmc.managers.GameManager;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;

@Plugin(id = "ClimaxGames", name = "ClimaxGames", version = "1.0")
public class ClimaxGames {
    @Inject
    private Logger logger;
    @Inject
    private Game game;

    private static ClimaxGames instance;

    private GameManager manager;

    @Subscribe
    public void onServerStarting(ServerStartingEvent event) {
        instance = this;

        manager = new GameManager();
    }

    @Subscribe
    public void onServerPreStart(ServerAboutToStartEvent event) {

    }

    @Subscribe
    public void onServerStopping(ServerStoppingEvent event) {
        game.getServer().getOnlinePlayers().forEach(player -> player.kick(Texts.builder("Server shutting down.").build()));
    }

    /**
     * Get the plugin instance
     *
     * @return Plugin instance
     */
    public static ClimaxGames getInstance() {
        return instance;
    }

    /**
     * Gets the plugin logger
     *
     * @return Plugin logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets the server game (not to be confused with Minigames)
     *
     * @return Server game
     */
    public Game getGame() {
        return game;
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
