package net.climaxmc.managers;

import net.climaxmc.command.commands.punishments.PunishType;
import net.climaxmc.command.commands.punishments.Time;
import net.climaxmc.events.GameStateChangeEvent;
import net.climaxmc.game.Game;
import net.climaxmc.kit.Kit;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.mysql.Rank;
import net.climaxmc.utilities.*;
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
import org.bukkit.scoreboard.*;

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

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.getObjective(DisplaySlot.SIDEBAR).getName().equals("GameLobby")) {
            Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
            for (String entry : scoreboard.getEntries()) {
                for (Score score : scoreboard.getScores(entry)) {
                    if (score.getScore() == 4) {
                        scoreboard.resetScores(score.getEntry());
                        objective.getScore(C.RED + C.BOLD + "Kit " + C.WHITE + "\u00bb " + kit.getName()).setScore(4);
                    }
                }
            }
        }
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
        });

        manager.getGame().getWorldConfig().getTeams().forEach(team -> {
            int i = 0;
            for (UUID playerUUID : team.getPlayers()) {
                Player player = plugin.getServer().getPlayer(playerUUID);
                player.teleport(team.getSpawns().get(++i));
                manager.getGame().getPlayerKits().get(playerUUID).apply(player);
            }
        });

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilChat.sendActionBar(player, "4")), 20);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilChat.sendActionBar(player, "3")), 40);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilChat.sendActionBar(player, "2")), 60);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> UtilPlayer.getAll().forEach(player -> UtilChat.sendActionBar(player, "1")), 80);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            UtilPlayer.getAll().forEach(player -> UtilChat.sendActionBar(player, C.BOLD + "GOOO"));
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
        PlayerData playerData = plugin.getPlayerData(event.getUniqueId());

        if (playerData == null) {
            return;
        }

        playerData.getPunishments().stream().filter(punishment -> punishment.getType().equals(PunishType.BAN)).forEach(punishment -> {
            PlayerData punisherData = plugin.getMySQL().getPlayerData(punishment.getPunisherID());
            if (System.currentTimeMillis() <= (punishment.getTime() + punishment.getExpiration())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, F.message("Punishments", C.RED + "You were temporarily banned by " + punisherData.getName()
                        + " for " + punishment.getReason() + ".\n"
                        + "You have " + Time.toString(punishment.getTime() + punishment.getExpiration() - System.currentTimeMillis()) + " left in your ban.\n"
                        + "Appeal on forum.climaxmc.net if you believe that this is in error!"));
            } else if (punishment.getExpiration() == -1) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, F.message("Punishments", C.RED + "You were permanently banned by " + punisherData.getName()
                        + " for " + punishment.getReason() + ".\n"
                        + "Appeal on forum.climaxmc.net if you believe that this is in error!"));
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getPlayerData(player) == null) {
            plugin.getMySQL().createPlayerData(player);
        }
        event.setJoinMessage(C.DARK_AQUA + "Join" + C.DARK_GRAY + "\u00bb " + player.getName());
        UtilPlayer.reset(player);
        if (manager.getGame().hasStarted()) {
            player.teleport(plugin.getServer().getWorld(manager.getGame().getName()).getSpawnLocation());
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
            manager.getGame().startCountdown();
            UtilPlayer.getAll().forEach(players -> {
                Scoreboard scoreboard = players.getScoreboard();
                if (scoreboard != null && scoreboard.getObjective(DisplaySlot.SIDEBAR) != null && scoreboard.getObjective(DisplaySlot.SIDEBAR).getName().equals("GameLobby")) {
                    Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
                    for (String entry : scoreboard.getEntries()) {
                        scoreboard.getScores(entry).stream().filter(score -> score.getScore() == 8).forEach(score -> {
                            scoreboard.resetScores(score.getEntry());
                            objective.getScore(C.RED + C.BOLD + "Players" + C.WHITE + " \u00bb " + C.YELLOW + UtilPlayer.getAll().size() + "/" + manager.getGame().getMaxPlayers()).setScore(8);
                        });
                    }
                }
            });
        }
        manager.getGame().getPlayerKits().put(player.getUniqueId(), manager.getGame().getKits()[0]);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> manager.initializeLobbyScoreboard(player), 2); // Slightly hacky
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(C.RED + "Quit" + C.DARK_GRAY + "\u00bb " + event.getPlayer().getName());

        UtilPlayer.getAll().forEach(players -> {
            Scoreboard scoreboard = players.getScoreboard();
            if (scoreboard.getObjective(DisplaySlot.SIDEBAR).getName().equals("GameLobby")) {
                Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
                for (String entry : scoreboard.getEntries()) {
                    scoreboard.getScores(entry).stream().filter(score -> score.getScore() == 8).forEach(score -> {
                        scoreboard.resetScores(score.getEntry());
                        objective.getScore(C.RED + C.BOLD + "Players" + C.WHITE + " \u00bb " + C.YELLOW + UtilPlayer.getAll().size() + "/" + manager.getGame().getMaxPlayers()).setScore(8);
                    });
                }
            }
        });
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
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerData(player);
        if (playerData.hasRank(Rank.NINJA)) {
            event.setFormat(C.DARK_GRAY + C.BOLD + "{" + playerData.getRank().getPrefix() + C.DARK_GRAY + C.BOLD + "} " + C.GRAY + "%s" + C.RESET + ": %s");
        } else {
            event.setFormat(C.GRAY + "%s" + C.RESET + ": %s");
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
}
