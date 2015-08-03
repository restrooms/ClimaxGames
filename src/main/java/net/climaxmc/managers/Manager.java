package net.climaxmc.managers;

import net.climaxmc.ClimaxGames;
import org.bukkit.event.Listener;

/**
 * Represents a manager
 */
public abstract class Manager implements Listener {
    static GameManager manager;
    ClimaxGames plugin = ClimaxGames.getInstance();
}
