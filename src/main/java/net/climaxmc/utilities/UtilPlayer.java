package net.climaxmc.utilities;

import net.climaxmc.ClimaxGames;
import net.climaxmc.mysql.Rank;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

public class UtilPlayer {
    public static List<Player> getAll() {
        return getAll(true);
    }

    public static List<Player> getAll(boolean includeSpectators) {
        List<Player> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(players::add);
        players.removeIf(player -> player.getGameMode().equals(GameMode.SPECTATOR) && !includeSpectators);
        return players;
    }

    public static List<Player> getAll(Rank rank) {
        return Bukkit.getOnlinePlayers().stream().filter(player -> ClimaxGames.getInstance().getPlayerData(player).hasRank(rank)).collect(Collectors.<Player>toList());
    }

    public static List<Player> getAllShuffled() {
        List<Player> players = getAll();
        Collections.shuffle(players);
        return players;
    }

    public static void reset(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
        for (PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
        player.setMaxHealth(20);
        player.setHealth(20);
    }
}
