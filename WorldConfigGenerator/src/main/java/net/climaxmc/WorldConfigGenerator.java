package net.climaxmc;

import net.climaxmc.commands.WorldConfigCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldConfigGenerator extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("worldconfig").setExecutor(new WorldConfigCommand(this));
    }
}
