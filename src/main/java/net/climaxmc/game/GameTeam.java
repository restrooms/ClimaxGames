package net.climaxmc.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

import java.util.*;

@Data
@AllArgsConstructor
public class GameTeam {
    private String name;
    private List<Location> spawns;
    private List<UUID> players;
}
