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

import java.util.*;

public class BasicGunAbility extends Ability {
    private Set<UUID> reloading = new HashSet<>();

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

        ItemStack snowballs = player.getInventory().getItem(2);
        if (snowballs != null && snowballs.getAmount() > 0) {
            Projectile snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(snowball.getVelocity().multiply(2));
            player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5f, 1.5f);
            snowballs.setAmount(snowballs.getAmount() - 1);
            player.getInventory().setItem(2, snowballs);
            reloading.remove(player.getUniqueId());
        } else if (!reloading.contains(player.getUniqueId())) {
            UtilChat.sendActionBar(player, C.RED + "Reloading...");
            reloading.add(player.getUniqueId());
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
                        player.getInventory().setItem(2, new ItemStack(Material.SNOW_BALL, 32));
                        cancel();
                        return;
                    }
                    timer--;
                }
            }.runTaskTimer(plugin, 5, 5);
        }
    }
}
