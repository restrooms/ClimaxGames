package net.climaxmc.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GameTeam {
    private String name;
    private List<Location> spawns;
    private List<UUID> players;
    private Color color;
    private String colorCode;
}
