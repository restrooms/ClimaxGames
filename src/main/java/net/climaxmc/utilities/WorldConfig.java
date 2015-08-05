package net.climaxmc.utilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class WorldConfig {
    private String mapName;
    private String authorName;
    private Map<String, List<Location>> teamSpawns;
}
