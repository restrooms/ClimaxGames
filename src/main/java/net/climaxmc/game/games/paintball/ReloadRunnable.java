package net.climaxmc.game.games.paintball;

import net.climaxmc.ClimaxGames;
import net.climaxmc.core.utilities.C;
import net.climaxmc.core.utilities.UtilPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

class ReloadRunnable extends BukkitRunnable {
    private Player player;
    private Paintball paintball;
    private int timer = 8;

    ReloadRunnable(ClimaxGames plugin, Paintball paintball, Player player) {
        this.paintball = paintball;
        this.player = player;

        if (!paintball.getReloading().contains(player.getUniqueId())) {
            runTaskTimer(plugin, 5, 5);
            UtilPlayer.sendActionBar(player, C.RED + "Reloading...");
            paintball.getReloading().add(player.getUniqueId());
        }
    }

    @Override
    public void run() {
        if (timer == 8) {
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 0.3F);
        } else if (timer == 4) {
            player.playSound(player.getLocation(), Sound.CLICK, 1F, 0.1F);
        } else if (timer == 1) {
            player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
        } else if (timer == 0) {
            player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
            UtilPlayer.sendActionBar(player, C.GREEN + "Done!");
            player.getInventory().setItem(2, new ItemStack(Material.SNOW_BALL, 45));
            paintball.getReloading().remove(player.getUniqueId());
            cancel();
            return;
        }
        timer--;
    }
}
