package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Perk;
import net.climaxmc.kit.perks.BasicGunPerk;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.ItemTypes;

public class BasicKit extends Kit {
    public BasicKit() {
        super("Basic", new String[]{"Basic paintball kit.", "Jonhan will change the name at some point."}, new Perk[]{new BasicGunPerk()});
    }

    @Override
    public void apply(ArmorEquipable entity) {
        entity.setHelmet(itemBuilder.itemType(ItemTypes.LEATHER_HELMET).build());
        entity.setChestplate(itemBuilder.itemType(ItemTypes.LEATHER_CHESTPLATE).build());
        entity.setLeggings(itemBuilder.itemType(ItemTypes.LEATHER_LEGGINGS).build());
        entity.setBoots(itemBuilder.itemType(ItemTypes.LEATHER_BOOTS).build());
        //entity.equip(EquipmentTypes.EQUIPPED, itemBuilder.itemType(ItemTypes.GOLDEN_HORSE_ARMOR).build());
        //entity.getInventory().offer(itemBuilder.itemType(ItemTypes.GOLDEN_HORSE_ARMOR)/*TODO .itemData()*/.build());
        //entity.getInventory().offer(itemBuilder.itemType(ItemTypes.POTION).quantity(3).itemData(plugin.getGame().getRegistry().getManipulatorRegistry().getBuilder(DisplayNameData.class).get().create().setDisplayName(Texts.builder("Revive Potion").color(TextColors.AQUA).build())).build());
    }
}
