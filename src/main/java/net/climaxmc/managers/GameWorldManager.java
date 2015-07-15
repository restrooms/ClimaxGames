package net.climaxmc.managers;

public class GameWorldManager implements Manager {
    private GameManager manager;

    public GameWorldManager(GameManager manager) {
        this.manager = manager;
    }
}
