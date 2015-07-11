package net.climaxmc.command.commands;

import net.climaxmc.ClimaxGames;
import net.climaxmc.command.Command;
import net.climaxmc.utilities.Rank;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.args.CommandElement;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.util.Arrays;
import java.util.HashMap;

public class GameCommand extends Command {
    public GameCommand(ClimaxGames plugin) {
        super(plugin, "game", new CommandElement[]{GenericArguments.string(Texts.of("start/stop"))});
    }

    @Override
    public void execute(Player player, CommandContext context) throws CommandException {
        String args = context.<String>getOne("start/stop").get();
    }
}
