package net.climaxmc.kit.perks;

import net.climaxmc.kit.Perk;
import net.climaxmc.utilities.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class ProfessionalGunPerk extends Perk {
    private Ability gun = new Ability(1, 2, TimeUnit.SECONDS);

    public ProfessionalGunPerk() {
        super("Advanced Gun");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted()) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.DIAMOND_BARDING)) {
            return;
        }

        /*if (!gun.tryUse(player)) {
            return;
        }*/

        // TODO: CREATE SNOWBALL
    }
}
