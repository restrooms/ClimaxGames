package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Perk;
import net.climaxmc.kit.perks.BasicGunPerk;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.ItemTypes;

public class BasicKit extends Kit {
    public BasicKit() {
        super("Basic", new String[]{"Basic paintball kit.", "Jonhan will change the name at some point."}, new Perk[]{new BasicGunPerk()});
    }

    @Override
    public void apply(Player player) {
        player.getInventory().offer(itemBuilder.itemType(ItemTypes.GOLDEN_HORSE_ARMOR)/*TODO .itemData()*/.build());
    }
}
