package net.climaxmc.commands;

import net.climaxmc.ClimaxGames;
import net.climaxmc.core.command.Command;
import net.climaxmc.core.mysql.Rank;
import net.climaxmc.core.utilities.C;
import net.climaxmc.core.utilities.F;
import net.climaxmc.game.Game;
import org.bukkit.Bukkit;
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

        Game game = ClimaxGames.getInstance().getManager().getGame();
        if (args[0].equalsIgnoreCase("start")) {
            if (!game.getState().equals(Game.GameState.READY)) {
                return C.BOLD + "The game has already started.";
            }

            game.setState(Game.GameState.PREPARE);
            Bukkit.broadcastMessage(C.GOLD + C.BOLD + player.getName() + " started the game.");
            return null;
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!game.getState().equals(Game.GameState.IN_GAME)) {
                return C.BOLD + "The game has not started yet.";
            }

            game.setState(Game.GameState.END);
            Bukkit.broadcastMessage(C.GOLD + C.BOLD + player.getName() + " stopped the game.");
            return null;
        }

        return usage;
    }
}
