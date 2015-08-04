package net.climaxmc.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class UtilPlayer {
    public static Collection<? extends Player> getAll() {
        return Bukkit.getOnlinePlayers();
    }
}
