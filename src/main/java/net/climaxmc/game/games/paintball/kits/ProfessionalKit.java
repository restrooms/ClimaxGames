package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Perk;
import net.climaxmc.kit.perks.ProfessionalGunPerk;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ProfessionalKit extends Kit {
    public ProfessionalKit() {
        super(C.RED + "Professional", new String[]{"Professional paintball kit.", "Jonhan will change the name at some point."}, new Perk[]{new ProfessionalGunPerk()}, new I(Material.DIAMOND_BARDING), 5000);
    }

    @Override
    public void giveItems(Player player) {
        player.getInventory().setHelmet(new I(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(new I(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(new I(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(new I(Material.LEATHER_BOOTS));
        player.getInventory().addItem(new I(Material.DIAMOND_BARDING).name(C.GOLD + "Professional Gun"));
        player.getInventory().addItem(new I(Material.POTION).name(C.BLUE + "Revive Potion"));
    }
}
