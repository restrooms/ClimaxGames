package net.climaxmc.kit.abilities;

import net.climaxmc.kit.Ability;
import net.climaxmc.utilities.UtilPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProfessionalGunAbility extends Ability {
    public ProfessionalGunAbility() {
        super("Professional Gun"/*, 2000*/);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted() || !UtilPlayer.getAll(false).contains(player)) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.DIAMOND_BARDING)) {
            return;
        }

        /*if (!cooldown.check(player)) {
            return;
        }*/

        // TODO: CREATE SNOWBALL
    }
}
