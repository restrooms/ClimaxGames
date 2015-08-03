package net.climaxmc.kit;

import net.climaxmc.ClimaxGames;
import org.bukkit.event.Listener;

/**
 * Represents a kit perk
 */
public abstract class Perk implements Listener {
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
