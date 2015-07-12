package net.climaxmc.game.games.paintball;

import net.climaxmc.ClimaxGames;
import net.climaxmc.game.Game;
import net.climaxmc.game.games.paintball.kits.BasicKit;
import net.climaxmc.kit.Kit;

public class Paintball extends Game {
    public Paintball(ClimaxGames plugin) {
        super(plugin, "Paintball", new Kit[]{new BasicKit()});
    }
}
