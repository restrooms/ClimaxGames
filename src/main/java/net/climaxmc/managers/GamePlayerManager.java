package net.climaxmc.managers;

import net.climaxmc.core.ClimaxCore;
import net.climaxmc.core.events.PlayerBalanceChangeEvent;
import net.climaxmc.core.mysql.PlayerData;
import net.climaxmc.core.mysql.Rank;
import net.climaxmc.core.utilities.*;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.kit.Kit;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Optional;
import java.util.UUID;

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

    @SuppressWarnings("deprecation")
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

        PlayerData playerData = ClimaxCore.getPlayerData(player);

        for (Kit possibleKit : manager.getGame().getKits()) {
            if (entity.getCustomName().contains("Kit") && entity.getCustomName().contains(possibleKit.getName())) {
                if (!possibleKit.hasKit(player, manager.getGame().getType())) {
                    if (playerData.getCoins() < possibleKit.getCost()) {
                        UtilPlayer.sendActionBar(player, F.message("Kit", C.RED + "You do not have enough " + C.BOLD + "C" + C.GOLD + C.BOLD + "Coins" + C.RED + " to complete that transaction!"));
                        return;
                    }
                    possibleKit.purchaseKit(player, manager.getGame().getType());
                    player.sendTitle("", F.message("Kit", C.GREEN + "You have purchased kit " + possibleKit.getName() + "!"));
                }
                kit = possibleKit;
            }
        }

        if (kit == null) {
            return;
        }

        kit.apply(player);
        kit.displayDescription(player);

        UtilPlayer.sendActionBar(player, F.message("Kit", "Selected: " + kit.getName()));

        manager.setPlayerLobbyScoreboardValue(player, 4, C.RED + C.BOLD + "Kit " + C.WHITE + "\u00bb " + kit.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameStart(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.PREPARE)) {
            return;
        }

        UtilPlayer.getAllShuffled().forEach(player -> {
            if (!manager.getGame().getPlayerKits().containsKey(player.getUniqueId())) {
                manager.getGame().getPlayerKits().put(player.getUniqueId(), manager.getGame().getKits()[0]);
            }
            player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
        });

        manager.getGame().getWorldConfig().getTeams().forEach(team -> {
            int i = 0;
            for (UUID playerUUID : team.getPlayers()) {
                Player player = plugin.getServer().getPlayer(playerUUID);
                if (player != null) {
                    if (team.getSpawns().size() <= i++) {
                        i = 1;
                    }
                    player.teleport(team.getSpawns().get(i));
                    manager.getGame().getPlayerKits().get(playerUUID).apply(player);
                }
            }
        });

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, "4")), 20);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, "3")), 40);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, "2")), 60);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, "1")), 80);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, C.BOLD + "GOOO"));
            manager.getGame().setState(Game.GameState.IN_GAME);
        }, 100);
    }

    @EventHandler
    public void onGameStop(GameStateChangeEvent event) {
        if (!event.getState().equals(Game.GameState.READY)) {
            return;
        }

        UtilPlayer.getAll().forEach(player -> {
            UtilPlayer.reset(player);
            player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        });
    }

    @EventHandler
    public void onPlayerMoveGameStarting(PlayerMoveEvent event) {
        if (manager.getGame().getState().equals(Game.GameState.PREPARE)) {
            Location location = event.getFrom();
            location.setYaw(event.getTo().getYaw());
            location.setPitch(event.getTo().getPitch());
            event.setTo(location);
        }
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PlayerData playerData = ClimaxCore.getPlayerData(event.getUniqueId());
        if (UtilPlayer.getAll().size() >= manager.getGame().getMaxPlayers() && playerData.getRank() == Rank.DEFAULT) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, C.RED + "Server is full!");
        }
        /*if (!playerData.hasRank(Rank.NINJA)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, C.RED + "Server requires beta access! Buy a rank at donate.climaxmc.net today! (Coming soon for non-donors)");
        }*/
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(C.DARK_AQUA + "Join" + C.DARK_GRAY + "\u00bb " + player.getName());
        UtilPlayer.reset(player);
        if (manager.getGame().hasStarted()) {
            player.teleport(plugin.getServer().getWorld(manager.getGame().getName()).getSpawnLocation());
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(F.message("Spectate", "You have spawned in spectator mode."));
        } else {
            player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
            manager.getGame().startCountdown();
            UtilPlayer.getAll().stream().filter(players -> players.getScoreboard() != null && players.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
                    .forEach(players -> manager.setPlayerLobbyScoreboardValue(players, 8, C.RED + C.BOLD + "Players" + C.WHITE + " \u00bb " + C.YELLOW + UtilPlayer.getAll().size() + "/" + manager.getGame().getMaxPlayers()));
        }
        manager.getGame().getPlayerKits().put(player.getUniqueId(), manager.getGame().getKits()[0]);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            manager.initializeLobbyScoreboard(player);
            if (ClimaxCore.getPlayerData(player).hasRank(Rank.OWNER) && !player.isOp()) {
                player.setOp(true);
            }
        }, 2); // Slightly hacky
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(C.RED + "Quit" + C.DARK_GRAY + "\u00bb " + player.getName());

        if (manager.getGame().hasStarted()) {
            player.setGameMode(GameMode.SPECTATOR);
        }

        UtilPlayer.getAll().stream().filter(players -> players.getScoreboard() != null).forEach(players -> manager.setPlayerLobbyScoreboardValue(players, 8, C.RED + C.BOLD + "Players" + C.WHITE + " \u00bb " + C.YELLOW + (UtilPlayer.getAll().size() - 1) + "/" + manager.getGame().getMaxPlayers()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getEntity().spigot().respawn();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (manager.getGame().getState().equals(Game.GameState.IN_GAME)) {
            event.setRespawnLocation(manager.getGame().getPlayerTeam(player).getSpawns().get(RandomUtils.nextInt(manager.getGame().getPlayerTeam(player).getSpawns().size())));
            manager.getGame().getPlayerKits().get(player.getUniqueId()).apply(player);
        } else {
            event.setRespawnLocation(plugin.getServer().getWorld("world").getSpawnLocation());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (!manager.getGame().hasStarted() || damaged.getWorld().getName().equals("world")) {
            event.setCancelled(true);
        } else if (manager.getGame().hasStarted() && manager.getGame().getPlayerTeam(damaged).equals(manager.getGame().getPlayerTeam(damager))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!manager.getGame().hasStarted() || player.getWorld().getName().equals("world")) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
            }
            event.setCancelled(true);
        } else if (manager.getGame().hasStarted()) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && !manager.getGame().isFallDamage()) {
                event.setCancelled(true);
            } else if (!manager.getGame().isRespawnOnDeath() && player.getHealth() - event.getFinalDamage() <= 0) {
                event.setCancelled(true);
                player.setGameMode(GameMode.SPECTATOR);
                player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 1, 1);
                Player killer = player.getKiller();
                if (killer != null) {
                    plugin.getServer().broadcastMessage(C.RED + player.getName() + C.GRAY + " was killed by " + C.GREEN + killer.getName());
                    manager.getGame().addCoins(killer, "Killing " + player.getName(), 10);
                }
            }
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

    @EventHandler
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player && !((Player) event.getRemover()).getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!manager.getGame().hasStarted()) {
            event.setCancelled(true);
        }

        if (manager.getGame().isCancelInteract() || !event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().equals(Material.TRAP_DOOR)) {
            event.setCancelled(true);
        }

        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.FIRE)) {
            event.setCancelled(true);
        }

        for (BlockFace face : BlockFace.values()) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getRelative(face) != null && event.getClickedBlock().getRelative(face).getType().equals(Material.FIRE)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBalanceChangeUpdateScoreboard(PlayerBalanceChangeEvent event) {
        OfflinePlayer player = event.getPlayer();
        if (player.isOnline()) {
            manager.setPlayerLobbyScoreboardValue(player.getPlayer(), 6, C.RED + C.BOLD + "C" + C.GOLD + C.BOLD + "Coins" + C.WHITE + " \u00bb " + C.YELLOW + event.getAmount());
        }
    }
}
