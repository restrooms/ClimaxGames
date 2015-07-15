package net.climaxmc.managers;

public class GameLobbyManager implements Manager {
    private GameManager manager;

    public GameLobbyManager(GameManager manager) {
        this.manager = manager;

        initializeSpawns();
    }

    private void initializeSpawns() {

    }
}
