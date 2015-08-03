package net.climaxmc.kit.perks;

import com.google.common.base.Optional;
import net.climaxmc.kit.Perk;
import net.climaxmc.utilities.Ability;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerInteractEvent;
import org.spongepowered.api.item.ItemTypes;

import java.util.concurrent.TimeUnit;

public class BasicGunPerk extends Perk {
    private Ability gun = new Ability(1, 4, TimeUnit.SECONDS);

    public BasicGunPerk() {
        super("Basic Gun");
    }

    @Subscribe
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();

        if (!player.getItemInHand().get().getItem().equals(ItemTypes.GOLDEN_HORSE_ARMOR)) {
            return;
        }

        /*if (!gun.tryUse(player)) {
            return;
        }*/

        Optional<Entity> snowballOptional = player.getWorld().createEntity(EntityTypes.SNOWBALL, player.getLocation().getPosition());

        if (snowballOptional.isPresent()) {
            Snowball snowball = (Snowball) snowballOptional.get();
            player.getWorld().spawnEntity(snowball);
            //snowball.getData(TargetedLocationData.class).get().setValue(player.getLocation().add(4, 2, 0));
            //snowball.getDamagingData().setDamage(3);
        }
    }
}
