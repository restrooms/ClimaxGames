package net.climaxmc.game.games.paintball;

import net.climaxmc.core.mysql.GameType;
import net.climaxmc.core.utilities.C;
import net.climaxmc.core.utilities.UtilPlayer;
import net.climaxmc.game.Game;
import net.climaxmc.game.games.paintball.kits.DoubleJumpKit;
import net.climaxmc.kit.Kit;
import org.bukkit.*;
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
    public void onPlayerInteract(PlayerInteractEvent event) {
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
    public void onPlayerRevived(ProjectileHitEvent event) {

    }

    @EventHandler
    public void onZombieDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.ZOMBIE)) {
            event.setCancelled(true);
        }
    }
}
