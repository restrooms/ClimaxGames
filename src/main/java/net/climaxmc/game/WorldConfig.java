package net.climaxmc.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.climaxmc.game.GameTeam;

import java.util.List;

@Data
@AllArgsConstructor
public class WorldConfig {
    private String mapName;
    private String authorName;
    private List<GameTeam> teams;
}
