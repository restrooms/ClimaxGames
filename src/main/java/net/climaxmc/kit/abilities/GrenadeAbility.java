package net.climaxmc.kit.abilities;

import net.climaxmc.kit.Ability;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

        event.setCancelled(false);
        player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
        item.setVelocity(item.getVelocity().multiply(2));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            item.getWorld().createExplosion(item.getLocation().getX(), item.getLocation().getY(), item.getLocation().getZ(), 2, false, false);
            item.remove();
        }, 30);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClickDropTNT(PlayerInteractEvent event) {
        Player player = event.getPlayer();

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTNTSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();

        if (!plugin.getManager().getGame().hasStarted() || !item.getItemStack().getType().equals(Material.TNT)) {
            return;
        }

        event.setCancelled(false);
    }
}
