package net.climaxmc.kit.abilities;

import net.climaxmc.ClimaxGames;
import net.climaxmc.kit.Ability;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.I;
import net.climaxmc.utilities.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AdvancedGunAbility extends Ability {

    ClimaxGames plugin;

    public AdvancedGunAbility(ClimaxGames plugin) {
        super("Advanced Gun"/*, 1500*/);
        this.plugin = plugin;
    }
    private int timer = 5;
    int shots = 12;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted() || !UtilPlayer.getAll(false).contains(player)) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.IRON_BARDING)) {
            return;
        }

        if (!cooldown.check(player)) {
            return;
        }
        if (shots > 0) {
            for (int i = 0; i < 8; ++i) {
                final Vector rand = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
                rand.multiply(0.4);
                final Projectile snowball = player.launchProjectile(Snowball.class);
                snowball.setVelocity(snowball.getVelocity().multiply(1).add(rand));
                player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.8f, 1.0f);
                shots--;
            }
        } else {
            player.sendMessage(C.RED + "Reloading...");
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if(timer > 1) {
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1F, 0.3F);
                        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 3));
                    }
                    if(timer == 0) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                        shots = 12;
                        player.sendMessage(C.GREEN + "Done!");
                        return;
                    }
                    timer--;
                }
            }, 20L, 20L);
        }
    }
}
