package net.climaxmc;

import lombok.Getter;
import net.climaxmc.core.ClimaxCore;
import net.climaxmc.managers.GameManager;
import net.climaxmc.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;

public class ClimaxGames extends JavaPlugin {
    @Getter
    private static ClimaxGames instance;

    @Getter
    private GameManager manager;

    @Override
    public void onEnable() {
        instance = this;
        manager = new GameManager();
        ClimaxCore.onEnable(this, manager.getGame().getType());
        new Updater(this);
    }

    @Override
    public void onDisable() {
        ClimaxCore.onDisable();
    }
}
