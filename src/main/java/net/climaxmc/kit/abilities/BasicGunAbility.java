package net.climaxmc.kit.abilities;

import net.climaxmc.kit.Ability;
import net.climaxmc.utilities.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BasicGunAbility extends Ability {
    public BasicGunAbility() {
        super("Basic Gun");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted() || !UtilPlayer.getAll(false).contains(player)) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.GOLD_BARDING)) {
            return;
        }

        event.setCancelled(true);

        ItemStack snowballs = player.getInventory().getItem(3);
        if (snowballs != null && snowballs.getAmount() > 0) {
            Projectile snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(snowball.getVelocity().multiply(2));
            player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5f, 1.5f);
            snowballs.setAmount(snowballs.getAmount() - 1);
        } else {
            UtilChat.sendActionBar(player, C.RED + "Reloading...");
            new BukkitRunnable() {
                private int timer = 12;

                @Override
                public void run() {
                    if (timer == 12) {
                        player.playSound(player.getLocation(), Sound.CLICK, 1F, 0.3F);
                    } else if (timer == 4) {
                        player.playSound(player.getLocation(), Sound.CLICK, 1F, 0.1F);
                    } else if (timer == 1) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                    } else if (timer == 0) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                        UtilChat.sendActionBar(player, C.GREEN + "Done!");
                        cancel();
                        return;
                    }
                    timer--;
                }
            }.runTaskTimer(plugin, 5, 5);
        }
    }
}
