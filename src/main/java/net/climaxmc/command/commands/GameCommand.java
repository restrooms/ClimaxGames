package net.climaxmc.command.commands;

import net.climaxmc.command.Command;
import net.climaxmc.game.Game;
import net.climaxmc.utilities.F;
import net.climaxmc.utilities.Rank;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextStyles;

public class GameCommand extends Command {
    public GameCommand() {
        super(new String[]{"game"}, Rank.ADMINISTRATOR, F.message("Administration", "/game <start/stop>"));
    }

    @Override
    public Text execute(Player player, String[] args) {
        if (args.length != 1) {
            return usage;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (!plugin.getMinigame().getState().equals(Game.GameState.READY)) {
                return Texts.builder("The game has already started.").style(TextStyles.BOLD).build();
            }

            plugin.getMinigame().setState(Game.GameState.STARTING);
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!plugin.getMinigame().getState().equals(Game.GameState.IN_GAME)) {
                return Texts.builder("The game has not started yet.").style(TextStyles.BOLD).build();
            }

            plugin.getMinigame().setState(Game.GameState.ENDING);
        }

        return usage;
    }
}
