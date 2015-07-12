package net.climaxmc.events;

import net.climaxmc.game.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Cancellable;

/**
 * Represents a change of game state
 */
public class GameStateChangeEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled = false;

    private Game game;
    private Game.GameState state;

    /**
     * Defines a change of game state
     *
     * @param game  Game that changed state
     * @param state State that game changed to
     */
    public GameStateChangeEvent(Game game, Game.GameState state) {
        this.game = game;
        this.state = state;
    }

    /**
     * Gets the game
     *
     * @return Game that changed state
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the state
     *
     * @return State the game changed to
     */
    public Game.GameState getState() {
        return state;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
