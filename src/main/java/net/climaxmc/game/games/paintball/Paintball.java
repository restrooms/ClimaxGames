package net.climaxmc.game.games.paintball;

import com.darkblade12.particleeffect.ParticleEffect;
import lombok.Getter;
import net.climaxmc.core.mysql.GameType;
import net.climaxmc.core.utilities.*;
import net.climaxmc.game.Game;
import net.climaxmc.game.GameTeam;
import net.climaxmc.game.games.paintball.kits.DoubleJumpKit;
import net.climaxmc.game.games.paintball.kits.GrenadeKit;
import net.climaxmc.kit.Kit;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class Paintball extends Game.TeamGame {
    @Getter
    private Set<UUID> reloading = new HashSet<>();
    private Set<Location> rollbackBlocks = new HashSet<>();
    private Set<DeadPlayer> deadPlayers = new HashSet<>();
    private Set<Projectile> potions = new HashSet<>();

    public Paintball() {
        super("Paintball", GameType.PAINTBALL, new Kit[]{new DoubleJumpKit(), new GrenadeKit()});
        naturalRegeneration = false;
        pvp = false;
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
        if (snowballs != null && snowballs.getAmount() > 0 && !reloading.contains(player.getUniqueId())) {
            Projectile snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(snowball.getVelocity().normalize().multiply(4));
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
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(team.getColor()), snowball.getLocation(), UtilPlayer.getAll());
                }
            }.runTaskTimer(plugin, 1, 1);
        } else {
            new ReloadRunnable(plugin, this, player);
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

        shooter.playSound(shooter.getLocation(), Sound.NOTE_PLING, 1, 4);
        event.setDamage(5);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!hasStarted()) {
            return;
        }

        if (player.getHealth() - event.getFinalDamage() > 0) {
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        deadPlayers.add(new DeadPlayer(this, player));
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

        Projectile snowball = event.getEntity();
        BlockIterator iterator = new BlockIterator(snowball.getWorld(), snowball.getLocation().toVector(), snowball.getVelocity().normalize(), 0.0D, 4);
        Block hit = null;

        while (iterator.hasNext()) {
            hit = iterator.next();
            if (hit.getTypeId() != 0) {
                break;
            }
        }

        if (hit == null) {
            return;
        }

        Map<Location, OldBlock> blockMap = new HashMap<>();
        for (Block block : UtilBlock.getInRadius(hit, 2).keySet()) {
            if (!block.getType().equals(Material.AIR) && !block.getType().equals(Material.BARRIER) && !block.getType().equals(Material.LADDER) && !rollbackBlocks.contains(block.getLocation())) {
                blockMap.put(block.getLocation(), new OldBlock(block.getType(), block.getData()));
                rollbackBlocks.add(block.getLocation());
                block.setType(Material.STAINED_CLAY);
                block.setData(color);
            }
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> blockMap.forEach((location, oldBlock) -> {
            rollbackBlocks.remove(location);
            Block block = location.getWorld().getBlockAt(location);
            block.setType(oldBlock.getType());
            block.setData(oldBlock.getData());
        }), 200);
    }

    @EventHandler
    public void onGunReload(PlayerDropItemEvent event) {
        if (!hasStarted()) {
            return;
        }

        Player player = event.getPlayer();

        if (!event.getItemDrop().getItemStack().getType().equals(Material.GOLD_BARDING)) {
            return;
        }

        new ReloadRunnable(plugin, this, player);
    }

    @EventHandler
    public void onZombieDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.ZOMBIE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionThrow(PlayerInteractEvent event) {
        if (!hasStarted()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.getItemInHand().getType().equals(Material.POTION)) {
            return;
        }

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.getInventory().remove(player.getItemInHand());
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
        potions.add(potion);
    }

    @EventHandler
    public void onPotionHit(ProjectileHitEvent event) {
        Player thrower = (Player) event.getEntity().getShooter();

        if (!event.getEntityType().equals(EntityType.SPLASH_POTION)) {
            return;
        }

        if (!potions.remove(event.getEntity())) {
            return;
        }

        thrower.setHealth(20);

        GameTeam throwerTeam = getPlayerTeam(thrower);

        if (throwerTeam == null) {
            return;
        }

        Iterator<DeadPlayer> copyIterator = this.deadPlayers.iterator();
        while (copyIterator.hasNext()) {
            DeadPlayer copy = copyIterator.next();
            GameTeam otherTeam = getPlayerTeam(copy.getPlayer());
            if (otherTeam != null) {
                if (!otherTeam.equals(throwerTeam)) {
                    continue;
                }
                if (copy.getZombie().getLocation().add(0.0, 1.0, 0.0).subtract(event.getEntity().getLocation()).length() > 3.0) {
                    continue;
                }
                copy.getPlayer().setGameMode(GameMode.SURVIVAL);
                copy.getPlayer().teleport(copy.getZombie());
                copy.getZombie().remove();
                copy.getArmorStand().remove();
                copyIterator.remove();
                addCoins(thrower, "Revived " + copy.getPlayer().getName(), 4);
            }
        }
        /*for (Player player : UtilPlayer.getAll()) {
            GameTeam otherTeam2 = getPlayerTeam(player);
            if (otherTeam2 != null) {
                if (!otherTeam2.equals(throwerTeam)) {
                    continue;
                }
                if (player.getLocation().add(0.0, 1.0, 0.0).subtract(event.getEntity().getLocation()).length() > 3.0) {
                    continue;
                }
                player.setGameMode(GameMode.SURVIVAL);
            }
        }*/
    }
}
