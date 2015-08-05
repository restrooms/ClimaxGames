package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Perk;
import net.climaxmc.kit.perks.BasicGunPerk;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BasicKit extends Kit {
    public BasicKit() {
        super(C.DARK_AQUA + "Basic", new String[]{"Basic paintball kit.", "Jonhan will change the name at some point."}, new Perk[]{new BasicGunPerk()}, new I(Material.GOLD_BARDING));
    }

    @Override
    public void giveItems(Player player) {
        player.getInventory().setHelmet(new I(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(new I(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(new I(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(new I(Material.LEATHER_BOOTS));
        player.getInventory().addItem(new I(Material.GOLD_BARDING).name(C.GOLD + "Basic Gun"));
        player.getInventory().addItem(new I(Material.POTION).amount(3).name(C.BLUE + "Water Bottle"));
    }
}
