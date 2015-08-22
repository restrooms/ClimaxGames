package net.climaxmc.game.games.paintball;

import com.darkblade12.particleeffect.ParticleEffect;
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
import org.bukkit.util.BlockIterator;

import java.util.*;

public class Paintball extends Game.TeamGame {
    private Set<UUID> revive = new HashSet<>();
    private Map<UUID, DeadPlayer> deadPlayers = new HashMap<>();
    private Set<UUID> reloading = new HashSet<>();
    private Set<Location> rollbackBlocks = new HashSet<>();

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
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(team.getColor()), snowball.getLocation(), UtilPlayer.getAll());
                }
            }.runTaskTimer(plugin, 1, 1);
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

        shooter.playSound(shooter.getLocation(), Sound.NOTE_PLING, 1, 4);
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
            if (!block.getType().equals(Material.AIR) && !block.getType().equals(Material.BARRIER) && !rollbackBlocks.contains(block.getLocation())) {
                blockMap.put(block.getLocation(), new OldBlock(block.getType(), block.getData()));
                rollbackBlocks.add(block.getLocation());
                block.setType(Material.STAINED_CLAY);
                block.setData(color);
            }
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            blockMap.forEach((location, oldBlock) -> {
                rollbackBlocks.remove(location);
                Block block = location.getWorld().getBlockAt(location);
                block.setType(oldBlock.getType());
                block.setData(oldBlock.getData());
            });
        }, 200);
    }

    @EventHandler
    public void onZombieDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.ZOMBIE)) {
            event.setCancelled(true);
        }
    }
}
