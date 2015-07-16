package net.climaxmc.managers;

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerDeathEvent;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerQuitEvent;
import org.spongepowered.api.event.entity.player.PlayerRespawnEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class GamePlayerManager extends Manager {
    GamePlayerManager() {

    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setNewMessage(Texts.builder("Join").color(TextColors.DARK_AQUA).append(Texts.builder("\u00bb " + event.getEntity().getName()).color(TextColors.DARK_GRAY).build()).build());
        manager.getGame().getKits()[0].apply(event.getEntity());
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
}
