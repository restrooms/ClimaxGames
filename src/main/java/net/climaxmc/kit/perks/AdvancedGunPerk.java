package net.climaxmc.kit.perks;

import net.climaxmc.kit.Perk;
import net.climaxmc.utilities.Ability;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class AdvancedGunPerk extends Perk {
    private Ability gun = new Ability(1, 4, TimeUnit.SECONDS);

    public AdvancedGunPerk() {
        super("Advanced Gun");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.getItemInHand().getType().equals(Material.IRON_BARDING)) {
            return;
        }

        if (!gun.tryUse(player)) {
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
