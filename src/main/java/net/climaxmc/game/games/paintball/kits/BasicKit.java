package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Perk;
import net.climaxmc.kit.perks.BasicGunPerk;
import org.spongepowered.api.data.manipulator.DisplayNameData;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventories;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class BasicKit extends Kit {
    public BasicKit() {
        super("Basic", new String[]{"Basic paintball kit.", "Jonhan will change the name at some point."}, new Perk[]{new BasicGunPerk()});
    }

    @Override
    public void apply(Player player) {
        player.getInventory().offer(itemBuilder.itemType(ItemTypes.GOLDEN_HORSE_ARMOR)/*TODO .itemData()*/.build());
        player.getInventory().offer(itemBuilder.itemType(ItemTypes.POTION).quantity(3).itemData(plugin.getGame().getRegistry().getManipulatorRegistry().getBuilder(DisplayNameData.class).get().create().setDisplayName(Texts.builder("Revive Potion").color(TextColors.AQUA).build())).build());
    }
}
