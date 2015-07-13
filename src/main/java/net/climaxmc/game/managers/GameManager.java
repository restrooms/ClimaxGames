package net.climaxmc.game.managers;

import net.climaxmc.ClimaxGames;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import org.spongepowered.api.event.Subscribe;

public class GameManager {
    private ClimaxGames plugin;

    public GameManager(ClimaxGames plugin) {
        this.plugin = plugin;

        new GamePlayerManager(this);
    }

    @Subscribe
    public void onGameStarting(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.STARTING)) {
            return;
        }
    }

    protected ClimaxGames getPlugin() {
        return plugin;
    }
}
