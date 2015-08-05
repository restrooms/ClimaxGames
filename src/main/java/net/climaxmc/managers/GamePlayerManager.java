package net.climaxmc.managers;

import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.game.GameTeam;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.*;

public class GamePlayerManager extends Manager {
    GamePlayerManager() {

    }

    @EventHandler
    public void onPlayerSelectKit(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        if (!entity.getType().equals(EntityType.VILLAGER)) {
            return;
        }

        Player player = event.getPlayer();

        selectKit(player, entity);
    }

    @EventHandler
    public void onPlayerLeftClickKit(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if (damager.getType() != EntityType.PLAYER || damaged.getType() != EntityType.VILLAGER) {
            return;
        }

        Player player = (Player) damager;

        event.setCancelled(true);
        selectKit(player, damaged);
    }

    private void selectKit(Player player, Entity entity) {
        Kit kit = null;

        Optional<Entity> armorStandOptional = entity.getNearbyEntities(1, 1, 1).stream().filter(possibleArmorStand -> possibleArmorStand.getType().equals(EntityType.ARMOR_STAND)).findFirst();
        if (!armorStandOptional.isPresent()) {
            return;
        }
        entity = armorStandOptional.get();

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

        UtilChat.sendActionBar(player, F.message("Kit", "Selected: " + kit.getName()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameStart(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.PREPARE)) {
            return;
        }

        UtilPlayer.getAll().forEach(player -> {
            GameTeam smallest = manager.getGame().getTeams().get(1);
            final List<UUID> smallestPlayers = smallest.getPlayers();
            Optional<GameTeam> smallestOptional = manager.getGame().getTeams().stream().filter(team -> team.getPlayers().size() < smallestPlayers.size()).findFirst();
            if (smallestOptional.isPresent()) {
                smallest = smallestOptional.get();
            }
            smallest.getPlayers().add(player.getUniqueId());
        });

        manager.getGame().getTeams().forEach(team -> {
            int i = 0;
            for (UUID player : team.getPlayers()) {
                plugin.getServer().getPlayer(player).teleport(team.getSpawns().get(++i));
            }
        });
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(C.DARK_AQUA + "Join" + C.DARK_GRAY + "\u00bb " + player.getName());
        if (manager.getGame().getState().equals(Game.GameState.READY)) {
            manager.getGame().startCountdown();
        }
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
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(ChatColor.GRAY + "%s" + ChatColor.RESET + ": " + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!manager.getGame().hasStarted() || event.getEntity().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!manager.getGame().hasStarted() || event.getEntity().getWorld().getName().equals("world")) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                event.getEntity().teleport(plugin.getServer().getWorld("world").getSpawnLocation());
            }
            event.setCancelled(true);
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Villager) {
            event.setCancelled(true);
        }
    }
}
