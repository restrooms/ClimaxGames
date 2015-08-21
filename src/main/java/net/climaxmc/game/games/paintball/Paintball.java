package net.climaxmc.game.games.paintball;

import net.climaxmc.core.mysql.GameType;
import net.climaxmc.core.utilities.*;
import net.climaxmc.game.Game;
import net.climaxmc.game.GameTeam;
import net.climaxmc.game.games.paintball.kits.DoubleJumpKit;
import net.climaxmc.kit.Kit;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Paintball extends Game.TeamGame {
    private Set<UUID> revive = new HashSet<>();
    private HashMap<UUID, DeadPlayer> deadPlayers = new HashMap<>();
    private Set<UUID> reloading = new HashSet<>();

    public Paintball() {
        super("Paintball", GameType.PAINTBALL, new Kit[]{new DoubleJumpKit()});
    }

    @EventHandler
    public void onPlayerLaunchSnowball(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getManager().getGame().hasStarted() || !UtilPlayer.getAll(false).contains(player)) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.GOLD_BARDING)) {
            return;
        }

        event.setCancelled(true);

        ItemStack snowballs = player.getInventory().getItem(2);
        if (snowballs != null && snowballs.getAmount() > 0) {
            Projectile snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(snowball.getVelocity().multiply(2));
            player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5f, 1.5f);
            snowballs.setAmount(snowballs.getAmount() - 1);
            player.getInventory().setItem(2, snowballs);
            reloading.remove(player.getUniqueId());
            GameTeam team = getPlayerTeam(player);
            new BukkitRunnable() {
                @Override
                @SuppressWarnings("deprecation")
                public void run() {
                    if (!snowball.isValid()) {
                        cancel();
                        return;
                    }
                    if (team.getName().equals("Blue")) {
                        snowball.getWorld().spigot().playEffect(snowball.getLocation(), Effect.WATERDRIP);
                    } else if (team.getName().equals("Red")) {
                        snowball.getWorld().spigot().playEffect(snowball.getLocation(), Effect.LAVA_POP);
                    }
                }
            }.runTaskTimer(plugin, 2, 1);
        } else if (!reloading.contains(player.getUniqueId())) {
            UtilPlayer.sendActionBar(player, C.RED + "Reloading...");
            reloading.add(player.getUniqueId());
            new BukkitRunnable() {
                private int timer = 12;

                @Override
                public void run() {
                    if (timer == 12) {
                        player.playSound(player.getLocation(), Sound.CLICK, 1F, 0.3F);
                    } else if (timer == 4) {
                        player.playSound(player.getLocation(), Sound.CLICK, 1F, 0.1F);
                    } else if (timer == 1) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                    } else if (timer == 0) {
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 1F, 1.3F);
                        UtilPlayer.sendActionBar(player, C.GREEN + "Done!");
                        player.getInventory().setItem(2, new ItemStack(Material.SNOW_BALL, 32));
                        cancel();
                        return;
                    }
                    timer--;
                }
            }.runTaskTimer(plugin, 5, 5);
        }
    }

    @EventHandler
    public void onPlayerDamageBySnowball(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getType().equals(EntityType.SNOWBALL) || !event.getEntityType().equals(EntityType.PLAYER)) {
            return;
        }

        Snowball snowball = (Snowball) event.getDamager();
        Player shooter = (Player) snowball.getShooter();

        if (getPlayerTeam(shooter).equals(getPlayerTeam((Player) event.getEntity()))) {
            event.setCancelled(true);
            return;
        }

        shooter.playSound(shooter.getLocation(), Sound.NOTE_PLING, 1, 3);
        event.setDamage(7);
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!hasStarted()) {
            return;
        }

        if (player.getHealth() - event.getFinalDamage() <= 0) {
            deadPlayers.put(player.getUniqueId(), new DeadPlayer(this, player));
        }
    }

    @EventHandler
    public void onPlayerRevive(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!hasStarted()) {
            return;
        }

        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }

        if (!player.getItemInHand().getType().equals(Material.POTION)) {
            return;
        }

        revive.add(player.getUniqueId());
        player.launchProjectile(ThrownPotion.class);

        player.getInventory().remove(Material.POTION);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onSnowballLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof ThrownPotion) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        byte color = 3;
        if (getPlayerTeam(player).getName().equals("Red")) {
            color = 14;
        }
        Location location = event.getEntity().getLocation().add(event.getEntity().getVelocity());
        for (Block block : UtilBlock.getInRadius(location, 1.5).keySet()) {
            if (!block.getType().equals(Material.STAINED_CLAY)) {
                block.getWorld().getBlockAt(block.getLocation()).setType(Material.STAINED_CLAY);
            }
            block.setData(color);
        }
        if (color == 3) {
            location.getWorld().playEffect(location, Effect.STEP_SOUND, 8);
        } else {
            location.getWorld().playEffect(location, Effect.STEP_SOUND, 10);
        }
    }

    @EventHandler
    public void onZombieDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.ZOMBIE)) {
            event.setCancelled(true);
        }
    }
}
