package net.climaxmc.managers;

import net.climaxmc.kit.Kit;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.*;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

public class GamePlayerManager extends Manager {
    GamePlayerManager() {

    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setNewMessage(Texts.builder("Join").color(TextColors.DARK_AQUA).append(Texts.builder("\u00bb " + event.getEntity().getName()).color(TextColors.DARK_GRAY).build()).build());
    }

    @Subscribe
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setNewMessage(Texts.builder("Quit").color(TextColors.RED).append(Texts.builder("\u00bb " + event.getEntity().getName()).color(TextColors.DARK_GRAY).build()).build());
    }

    @Subscribe
    public void onPlayerDeath(PlayerDeathEvent event) {

    }

    @Subscribe
    public void onPlayerRespawn(PlayerRespawnEvent event) {

    }

    @Subscribe
    public void onPlayerSelectKit(PlayerInteractEntityEvent event) {
        if (!event.getTargetEntity().getType().equals(EntityTypes.ZOMBIE)) {
            return;
        }

        Player player = event.getEntity();
        Kit kit = manager.getGame().getKits()[0];

        kit.apply(player);
        player.sendMessage(ChatTypes.ACTION_BAR, "You have selected " + kit.getName() + ".");
    }
}
