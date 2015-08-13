package net.climaxmc.game.games.paintball;

import net.climaxmc.game.*;
import net.climaxmc.game.games.paintball.kits.DoubleJumpKit;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.*;
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
            UtilChat.sendActionBar(player, C.RED + "Reloading...");
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
                        UtilChat.sendActionBar(player, C.GREEN + "Done!");
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
        /*if (!event.getEntityType().equals(EntityType.SPLASH_POTION)) {
            return;
        }

        ThrownPotion potion = (ThrownPotion) event.getEntity();

        if (potion.getShooter() == null || !(potion.getShooter() instanceof Player)) {
            return;
        }

        Player thrower = (Player) potion.getShooter();

        if (!revive.remove(thrower.getUniqueId())) {
            return;
        }

        GameTeam throwerTeam = getPlayerTeam(thrower);

        if (!(event.getEntity() instanceof Zombie)) {
            return;
        }

        Zombie zombie = (Zombie) event.getEntity();

        Optional<Entity> armorStandOptional = zombie.getNearbyEntities(1, 1, 1).stream().filter(armorStand -> armorStand.getType().equals(EntityType.ARMOR_STAND)).findFirst();

        if (!armorStandOptional.isPresent() || !armorStandOptional.get().getCustomName().startsWith(throwerTeam.getColorCode())) {
            armorStandOptional.get().remove();
            return;
        }

        Player player = plugin.getServer().getPlayer(zombie.getCustomName().substring(2));

        if (player == null) {
            return;
        }

        zombie.remove();
        player.sendMessage(F.message("Game", "You have been revived!"));
        player.teleport(zombie.getLocation());
        player.setGameMode(GameMode.SURVIVAL);*/

        if (!this.revive.remove(event.getEntity().getUniqueId())) {
            return;
        }
        if (event.getEntity().getShooter() == null) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player thrower = (Player)event.getEntity().getShooter();
        GameTeam throwerTeam = this.getPlayerTeam(thrower);
        if (throwerTeam == null) {
            return;
        }
        for (DeadPlayer copy : deadPlayers.values()) {
            final GameTeam otherTeam = this.getPlayerTeam(copy.getPlayer());
            if (otherTeam != null) {
                if (!otherTeam.equals(throwerTeam)) {
                    continue;
                }
                if (UtilMath.offset(copy.getZombie().getLocation().add(0.0, 1.0, 0.0), event.getEntity().getLocation()) > 3.0) {
                    continue;
                }
                copy.getPlayer().setGameMode(GameMode.SURVIVAL);
                deadPlayers.remove(copy.getPlayer().getUniqueId());
            }
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
