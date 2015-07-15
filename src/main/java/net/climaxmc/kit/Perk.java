package net.climaxmc.kit;

import net.climaxmc.ClimaxGames;

/**
 * Represents a kit perk
 */
public abstract class Perk {
    protected static ClimaxGames plugin = ClimaxGames.getInstance();

    private String name;

    /**
     * Defines a perk
     *
     * @param name Name of perk
     */
    public Perk(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the name of the perk
     *
     * @return Name of perk
     */
    public String getName() {
        return name;
    }
}
