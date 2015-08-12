package net.climaxmc.game.games.paintball;

import net.climaxmc.game.*;
import net.climaxmc.game.games.paintball.kits.AdvancedKit;
import net.climaxmc.game.games.paintball.kits.BasicKit;
import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class Paintball extends Game.TeamGame {
    private Set<UUID> revive = new HashSet<>();
    private HashMap<UUID, DeadPlayer> deadPlayers = new HashMap<>();

    public Paintball() {
        super("Paintball", GameType.PAINTBALL, new Kit[]{new BasicKit(), new AdvancedKit()});
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

        event.setDamage(3);
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

        Player killer = player.getKiller();
        if (killer != null) {
            addCoins(killer, "Killing " + player.getName(), 10);
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
