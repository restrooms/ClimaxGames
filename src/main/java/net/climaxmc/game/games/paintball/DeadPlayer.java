package net.climaxmc.game.games.paintball;

import lombok.AccessLevel;
import lombok.Getter;
import net.climaxmc.core.utilities.UtilEntity;
import org.bukkit.entity.*;

@Getter
class DeadPlayer {
    @Getter(value = AccessLevel.NONE)
    private Paintball paintball;
    private Zombie zombie;
    private ArmorStand armorStand;
    private Player player;

    DeadPlayer(Paintball paintball, Player player) {
        this.paintball = paintball;
        this.player = player;

        zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
        UtilEntity.removeAI(zombie);
        zombie.setBaby(false);
        zombie.setVillager(false);
        zombie.getEquipment().setArmorContents(player.getInventory().getArmorContents());

        armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setCustomName(paintball.getPlayerTeam(player).getColorCode() + player.getName());
        armorStand.setCustomNameVisible(true);
    }
}