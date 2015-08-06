package net.climaxmc.utilities;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;

public class UtilPlayer {
    public static Set<Player> getAll() {
        return getAll(true);
    }

    public static Set<Player> getAll(boolean includeSpectators) {
        Set<Player> players = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(players::add);
        players.removeIf(player -> player.getGameMode().equals(GameMode.SPECTATOR) && !includeSpectators);
        return players;
    }

    public static void reset(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
        player.setMaxHealth(20);
        player.setHealth(20);
    }
}
