package net.climaxmc;

import com.google.inject.Inject;
import net.climaxmc.managers.GameManager;
import org.slf4j.Logger;
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
    private org.spongepowered.api.Game game;

    private static ClimaxGames instance;
    private net.climaxmc.game.Game minigame;
    private GameManager manager;

    @Subscribe
    public void onServerStarting(ServerStartingEvent event) {
        manager = new GameManager(this);
    }

    @Subscribe
    public void onServerPreStart(ServerAboutToStartEvent event) {

    }

    @Subscribe
    public void onServerStopping(ServerStoppingEvent event) {
        game.getServer().getOnlinePlayers().forEach(player -> player.kick(Texts.builder("Server shutting down.").build()));
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
     * Get the plugin instance
     *
     * @return Plugin instance
     */
    public static ClimaxGames getInstance() {
        return instance;
    }

    /**
     * Gets the game (not to be confused with Minigames)
     *
     * @return Game
     */
    public org.spongepowered.api.Game getGame() {
        return game;
    }

    /**
     * Gets the current minigame of the server
     *
     * @return Current minigame
     */
    public net.climaxmc.game.Game getMinigame() {
        return minigame;
    }
}
