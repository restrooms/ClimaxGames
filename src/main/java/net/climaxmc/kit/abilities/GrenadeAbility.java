package net.climaxmc.kit.abilities;

import net.climaxmc.core.utilities.UtilAction;
import net.climaxmc.core.utilities.UtilParticle;
import net.climaxmc.game.games.paintball.TNTParticle;
import net.climaxmc.kit.Ability;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GrenadeAbility extends Ability {
    public GrenadeAbility() {
        super("Grenade");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropTNT(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();

        if (!plugin.getManager().getGame().hasStarted() || !item.getItemStack().getType().equals(Material.TNT)) {
            return;
        }

        spawnTNT(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClickDropTNT(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted() || !event.getItem().getType().equals(Material.TNT)) {
            return;
        }

        spawnTNT(player);
    }

    private void spawnTNT(Player player) {
        player.playSound(player.getLocation(), Sound.CREEPER_HISS, 1, 1);
        ItemStack tntInInv = player.getInventory().getItem(3);
        tntInInv.setAmount(tntInInv.getAmount() - 1);
        player.getInventory().setItem(3, tntInInv);
        TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), TNTPrimed.class);
        UtilAction.velocity(tnt, player.getLocation().getDirection().multiply(2), 0.5, false, 0.0, 0.1, 10.0, false);
        UtilAction.velocity(player, player.getLocation().getDirection().multiply(-1), tnt.getVelocity().length() + 0.02, false, 0.0, 0.2, 0.8, true);
        new TNTParticle(UtilParticle.ParticleType.REDSTONE, tnt, plugin.getManager().getGame().getPlayerTeam(player));
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        event.setCancelled(true);
        event.getLocation().getWorld().createExplosion(event.getLocation().getX(), event.getLocation().getY(), event.getLocation().getZ(), 6, false, false);
    }
}
