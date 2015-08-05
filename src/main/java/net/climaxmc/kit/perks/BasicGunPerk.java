package net.climaxmc.kit.perks;

import net.climaxmc.kit.Perk;
import net.climaxmc.utilities.Ability;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class BasicGunPerk extends Perk {
    private Ability gun = new Ability(1, 1, TimeUnit.SECONDS);

    public BasicGunPerk() {
        super("Basic Gun");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted()) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.GOLD_BARDING)) {
            return;
        }

        if (!gun.tryUse(player)) {
            return;
        }

        event.setCancelled(true);

        Projectile snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(snowball.getVelocity().multiply(2));
        player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5f, 1.5f);
    }
}
