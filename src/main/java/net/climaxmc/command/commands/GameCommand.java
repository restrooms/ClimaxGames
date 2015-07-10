package net.climaxmc.command.commands;

import net.climaxmc.ClimaxGames;
import net.climaxmc.command.Command;
import net.climaxmc.utilities.Rank;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.args.CommandContext;

public class GameCommand extends Command {
    public GameCommand(ClimaxGames plugin) {
        super(plugin, new String[] {"game"}, Rank.ADMINISTRATOR);
    }

    @Override
    public void execute(Player player, CommandContext context) throws CommandException {

    }
}
