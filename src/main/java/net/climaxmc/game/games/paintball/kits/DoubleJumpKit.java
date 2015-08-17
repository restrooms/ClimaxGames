package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.core.utilities.C;
import net.climaxmc.core.utilities.I;
import net.climaxmc.kit.Ability;
import net.climaxmc.kit.Kit;
import net.climaxmc.kit.abilities.DoubleJumpAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DoubleJumpKit extends Kit {
    public DoubleJumpKit() {
        super(C.GOLD + "Double Jump", new String[] {"Maneuver with ease and", "Take out enemies with your pistol!"}, new Ability[]{new DoubleJumpAbility()}, new I(Material.GOLD_BARDING));
    }

    @Override
    public void giveItems(Player player) {
        player.getInventory().addItem(new I(Material.GOLD_BARDING).name(C.GOLD + "Pistol"));
        player.getInventory().addItem(new I(Material.POTION).name(C.BLUE + "Revive Potion").amount(5));
        player.getInventory().addItem(new I(Material.SNOW_BALL).name(C.YELLOW + "Ammo").amount(32));
        player.getInventory().setHelmet(new I(Material.LEATHER_HELMET).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setChestplate(new I(Material.LEATHER_CHESTPLATE).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setLeggings(new I(Material.LEATHER_LEGGINGS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setBoots(new I(Material.LEATHER_BOOTS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
    }
}
