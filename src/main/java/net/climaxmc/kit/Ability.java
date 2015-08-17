package net.climaxmc.kit;

import net.climaxmc.ClimaxGames;
import net.climaxmc.core.utilities.Cooldown;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

/**
 * Represents a kit ability
 */
public abstract class Ability implements Listener {
    protected static ClimaxGames plugin = ClimaxGames.getInstance();

    private String name;

    protected Cooldown cooldown;

    /**
     * Defines an ability
     *
     * @param name Ability name
     */
    public Ability(String name) {
        this(name, 0);
    }

    /**
     * Defines an ability
     *
     * @param name Ability name
     * @param cooldownTime Ability cooldown time
     */
    public Ability(String name, int cooldownTime) {
        this.name = name;
        cooldown = new Cooldown(1, cooldownTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the name of the ability
     *
     * @return Name of ability
     */
    public String getName() {
        return name;
    }
}
