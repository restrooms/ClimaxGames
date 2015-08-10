package net.climaxmc.kit.abilities;

import net.climaxmc.kit.Ability;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BasicGunAbility extends Ability {

    public BasicGunAbility() {
        super("Basic Gun");
    }

    private int timer = 12;
    int shots = 32;

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

        if (shots > 0) {
            Projectile snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(snowball.getVelocity().multiply(2));
            player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5f, 1.5f);
            shots--;
        } else {
            player.sendMessage(C.RED + "Reloading...");
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (timer > 12) {
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1F, 0.3F);
                    }

                    if (timer > 4) {
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1F, 0.1F);
                        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 32));
                    }

                    if (timer == 1) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                    }

                    if (timer == 0) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                        shots = 32;
                        player.sendMessage(C.GREEN + "Done!");
                        return;
                    }
                    timer--;
                }
            }, 5L, 5L);
        }
    }
}
