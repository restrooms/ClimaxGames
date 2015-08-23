package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.core.utilities.C;
import net.climaxmc.core.utilities.I;
import net.climaxmc.kit.Ability;
import net.climaxmc.kit.Kit;
import net.climaxmc.kit.abilities.DoubleJumpAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DoubleJumpKit extends Kit {
    public DoubleJumpKit() {
        super(C.GOLD + "Double Jump", new String[] {"Maneuver with ease and", "Take out enemies with your pistol!", "Double press space to double jump."}, new Ability[]{new DoubleJumpAbility()}, new I(Material.GOLD_BARDING));
    }

    @Override
    public void giveItems(Player player) {
        player.getInventory().addItem(new I(Material.GOLD_BARDING).name(C.GOLD + "Pistol"));
        player.getInventory().addItem(new I(Material.POTION).name(C.BLUE + "Revive Potion").amount(2));
        player.getInventory().addItem(new I(Material.SNOW_BALL).name(C.YELLOW + "Ammo").amount(45));
        player.getInventory().setHelmet(new I(Material.LEATHER_HELMET).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setChestplate(new I(Material.LEATHER_CHESTPLATE).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setLeggings(new I(Material.LEATHER_LEGGINGS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setBoots(new I(Material.LEATHER_BOOTS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }
}
