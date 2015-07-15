package net.climaxmc.game;

import net.climaxmc.game.games.paintball.Paintball;

public enum GameType {
    PAINTBALL(Paintball.class, "Paintball");

    private Class<? extends Game> game;
    private String name;
    private boolean enabled = false;

    GameType(Class<? extends Game> game, String name) {
        this.game = game;
        this.name = name;
    }

    /**
     * Get the game class
     *
     * @return Game class
     */
    public Class<? extends Game> getGame() {
        return game;
    }

    /**
     * Get the game name
     *
     * @return Game name
     */
    public String getName() {
        return name;
    }

    /**
     * Get if the game is enabled
     *
     * @return Game is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the game enabled
     */
    public void setEnabled(boolean enable) {
        enabled = enable;
    }
}
