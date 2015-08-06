package net.climaxmc.game.games.paintball;

import net.climaxmc.game.Game;
import net.climaxmc.game.games.paintball.kits.*;
import net.climaxmc.kit.Kit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Paintball extends Game.TeamGame {
    public Paintball() {
        super("Paintball", new Kit[]{new BasicKit(), new AdvancedKit(), new ProfessionalKit()});
    }

    @EventHandler
    public void onPlayerDamageBySnowball(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getType().equals(EntityType.SNOWBALL) || !event.getEntityType().equals(EntityType.PLAYER)) {
            return;
        }

        Snowball snowball = (Snowball) event.getDamager();
        Player shooter = (Player) snowball.getShooter();

        if (getPlayerTeam(shooter).equals(getPlayerTeam((Player) event.getEntity()))) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(3);
    }
}
