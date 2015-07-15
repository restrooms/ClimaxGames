package net.climaxmc.managers;

public class GameCreationManager implements Manager {
    private GameManager manager;

    public GameCreationManager(GameManager manager) {
        this.manager = manager;
    }
}
