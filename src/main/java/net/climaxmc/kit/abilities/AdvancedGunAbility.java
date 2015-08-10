package net.climaxmc.kit.abilities;

import net.climaxmc.kit.Ability;
import net.climaxmc.utilities.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AdvancedGunAbility extends Ability {
    public AdvancedGunAbility() {
        super("Advanced Gun");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted() || !UtilPlayer.getAll(false).contains(player)) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.IRON_BARDING)) {
            return;
        }

        ItemStack snowballs = null;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack.getType().equals(Material.SNOW_BALL)) {
                snowballs = itemStack;
            }
        }
        if (snowballs != null && snowballs.getAmount() > 0) {
            for (int i = 0; i < 8; ++i) {
                Vector rand = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
                rand.multiply(0.4);
                Projectile snowball = player.launchProjectile(Snowball.class);
                snowball.setVelocity(snowball.getVelocity().multiply(1).add(rand));
                player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.8f, 1.0f);
                snowballs.setAmount(snowballs.getAmount() - 1);
            }
        } else {
            UtilChat.sendActionBar(player, C.RED + "Reloading...");
            new BukkitRunnable() {
                private int timer = 4;

                @Override
                public void run() {
                    if (timer > 0){
                        player.playSound(player.getLocation(), Sound.CLICK, 1F, 0.3F);
                    } else if (timer == 0) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                        UtilChat.sendActionBar(player, C.GREEN + "Done!");
                        cancel();
                        return;
                    }
                    timer--;
                }
            }.runTaskTimer(plugin, 20L, 20L);
        }
    }
}
