package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Perk;
import net.climaxmc.kit.perks.BasicGunPerk;
import net.climaxmc.utilities.I;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BasicKit extends Kit {
    public BasicKit() {
        super("Basic", new String[]{"Basic paintball kit.", "Jonhan will change the name at some point."}, new Perk[]{new BasicGunPerk()}, EntityType.ZOMBIE, new I(Material.GOLD_BARDING));
    }

    @Override
    public void apply(Player player) {
        player.getInventory().setHelmet(new I(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(new I(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(new I(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(new I(Material.LEATHER_BOOTS));
        //entity.equip(EquipmentTypes.EQUIPPED, itemBuilder.itemType(ItemTypes.GOLDEN_HORSE_ARMOR).build());
        //entity.getInventory().offer(itemBuilder.itemType(ItemTypes.GOLDEN_HORSE_ARMOR)/*TODO .itemData()*/.build());
        //entity.getInventory().offer(itemBuilder.itemType(ItemTypes.POTION).quantity(3).itemData(plugin.getGame().getRegistry().getManipulatorRegistry().getBuilder(DisplayNameData.class).get().create().setDisplayName(Texts.builder("Revive Potion").color(TextColors.AQUA).build())).build());
    }
}
