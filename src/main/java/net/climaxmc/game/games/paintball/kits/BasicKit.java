package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Perk;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStackBuilder;

public class BasicKit extends Kit {
    public BasicKit() {
        super("Basic", new String[] {"Basic paintball kit.", "Jonhan will change the name at some point."}, new Perk[] {});
    }

    @Override
    public void apply(Player player) {

    }
}
