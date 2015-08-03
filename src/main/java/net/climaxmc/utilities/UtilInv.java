package net.climaxmc.utilities;

import org.bukkit.entity.Player;

public class UtilInv {
    public static void clear(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }
}
