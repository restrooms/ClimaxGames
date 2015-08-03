package net.climaxmc.game.games.paintball;

import net.climaxmc.game.Game;
import net.climaxmc.game.games.paintball.kits.*;
import net.climaxmc.kit.Kit;

public class Paintball extends Game {
    public Paintball() {
        super("Paintball", new Kit[]{new BasicKit(), new AdvancedKit(), new ProfessionalKit()});
    }
}
