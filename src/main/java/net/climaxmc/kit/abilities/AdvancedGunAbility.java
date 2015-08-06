package net.climaxmc.kit.abilities;

import net.climaxmc.kit.Ability;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class AdvancedGunAbility extends Ability {
    public AdvancedGunAbility() {
        super("Advanced Gun", 1500);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted()) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.IRON_BARDING)) {
            return;
        }

        if (!cooldown.check(player)) {
            return;
        }

        for (int i = 0; i < 8; ++i) {
            final Vector rand = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
            rand.multiply(0.4);
            final Projectile snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(snowball.getVelocity().multiply(1).add(rand));
            player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.8f, 1.0f);
        }
    }
}
