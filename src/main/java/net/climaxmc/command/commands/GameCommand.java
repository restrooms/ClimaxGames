package net.climaxmc.command.commands;

import net.climaxmc.command.Command;
import net.climaxmc.game.Game;
import net.climaxmc.utilities.*;
import org.bukkit.entity.Player;

public class GameCommand extends Command {
    public GameCommand() {
        super(new String[]{"game"}, Rank.ADMINISTRATOR, F.message("Administration", "/game <start/stop>"));
    }

    @Override
    public String execute(Player player, String[] args) {
        if (args.length != 1) {
            return usage;
        }

        Game game = plugin.getManager().getGame();

        if (args[0].equalsIgnoreCase("start")) {
            if (!game.getState().equals(Game.GameState.READY)) {
                return C.BOLD + "The game has already started.";
            }

            game.setState(Game.GameState.STARTING);
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!game.getState().equals(Game.GameState.IN_GAME)) {
                return C.BOLD + "The game has not started yet.";
            }

            game.setState(Game.GameState.ENDING);
        }

        return usage;
    }
}
