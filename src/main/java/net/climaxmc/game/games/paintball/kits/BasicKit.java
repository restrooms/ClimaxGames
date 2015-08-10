package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Ability;
import net.climaxmc.kit.abilities.BasicGunAbility;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BasicKit extends Kit {
    public BasicKit() {
        super(C.DARK_AQUA + "Basic", new String[]{"Basic paintball kit.", "Jonhan will change the name at some point."}, new Ability[]{new BasicGunAbility()}, new I(Material.GOLD_BARDING));
    }

    @Override
    public void giveItems(Player player) {
        player.getInventory().setHelmet(new I(Material.LEATHER_HELMET).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setChestplate(new I(Material.LEATHER_CHESTPLATE).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setLeggings(new I(Material.LEATHER_LEGGINGS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setBoots(new I(Material.LEATHER_BOOTS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().addItem(new I(Material.GOLD_BARDING).name(C.GOLD + "Basic Gun"));
        player.getInventory().addItem(new I(Material.POTION).name(C.BLUE + "Revive Potion"));
        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 32));
    }
}
