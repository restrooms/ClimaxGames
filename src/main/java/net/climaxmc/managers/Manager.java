package net.climaxmc.managers;

import net.climaxmc.ClimaxGames;

/**
 * Represents a manager
 */
public abstract class Manager {
    ClimaxGames plugin = ClimaxGames.getInstance();
    static GameManager manager;
}
