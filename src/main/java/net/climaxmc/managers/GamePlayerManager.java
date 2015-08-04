package net.climaxmc.managers;

import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.*;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class GamePlayerManager extends Manager {
    GamePlayerManager() {

    }

    @EventHandler
    public void onPlayerSelectKit(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        if (!entity.getType().equals(EntityType.ZOMBIE)) {
            return;
        }

        Player player = event.getPlayer();

        selectKit(player, entity);
    }

    @EventHandler
    public void onPlayerLeftClickKit(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if (damager.getType() != EntityType.PLAYER || damaged.getType() != EntityType.ZOMBIE) {
            return;
        }

        Player player = (Player) damager;

        event.setCancelled(true);
        selectKit(player, damaged);
    }

    private void selectKit(Player player, Entity entity) {
        Kit kit = null;

        if (entity.getCustomName() == null) {
            return;
        }

        for (Kit possibleKit : manager.getGame().getKits()) {
            if (entity.getCustomName().contains("Kit") && entity.getName().split(" ")[0].contains(possibleKit.getName())) {
                kit = possibleKit;
            }
        }

        if (kit == null) {
            return;
        }

        kit.apply(player);
        kit.displayDescription(player);

        UtilChat.sendActionBar(player, F.message("Kits", "You have selected " + kit.getName() + "."));
    }

    @EventHandler
    public void onGameStart(GameStateChangeEvent event) {

    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(C.DARK_AQUA + "Join" + C.DARK_GRAY + "\u00bb " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(C.RED + "Quit" + C.DARK_GRAY + "\u00bb " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (manager.getGame().getState().equals(Game.GameState.IN_GAME)) {
            event.setRespawnLocation(plugin.getServer().getWorld(manager.getGame().getName()).getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLobbyConsumeItem(PlayerItemConsumeEvent event) {
        if (!manager.getGame().getState().equals(Game.GameState.IN_GAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }
}
