package net.climaxmc.managers;

import net.climaxmc.kit.Kit;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.F;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class GamePlayerManager extends Manager {
    GamePlayerManager() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(C.DARK_AQUA + "Join" + C.DARK_GRAY + "\u00bb " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(C.RED + "Quit" + C.DARK_GRAY + "\u00bb " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

    }

    @EventHandler
    public void onPlayerSelectKit(PlayerInteractEntityEvent event) {
        if (!event.getRightClicked().getType().equals(EntityType.ZOMBIE)) {
            return;
        }

        Player player = event.getPlayer();
        Kit kit = manager.getGame().getKits()[0];

        kit.apply(player);
        player.sendMessage(F.message("Kits", "You have selected " + kit.getName() + "."));
    }
}
