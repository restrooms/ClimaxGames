package net.climaxmc.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.*;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class GameTeam {
    private String name;
    private List<Location> spawns;
    private List<UUID> players;
    private Color color;
    private String colorCode;

    public List<UUID> getPlayers(boolean includeSpectators) {
        return players.stream().filter(playerUUID -> includeSpectators || !Bukkit.getPlayer(playerUUID).getGameMode().equals(GameMode.SPECTATOR)).collect(Collectors.toList());
    }
}
