package net.climaxmc.kit.abilities;

import net.climaxmc.kit.Ability;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class DoubleJumpAbility extends Ability {
    public DoubleJumpAbility() {
        super("Double Jump");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            if (player.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.AIR && !player.isFlying()) {
                player.setAllowFlight(true);
            }
        }
        if (!plugin.getManager().getGame().hasStarted() && !player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (!plugin.getManager().getGame().hasStarted()) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1, 1);
            event.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setVelocity(player.getLocation().getDirection().multiply(1.3).setY(1));
        }
    }
}
