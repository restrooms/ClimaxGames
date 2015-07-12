package net.climaxmc.command.commands;

import net.climaxmc.ClimaxGames;
import net.climaxmc.command.Command;
import net.climaxmc.game.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.args.CommandElement;
import org.spongepowered.api.util.command.args.GenericArguments;

import java.util.HashMap;

public class GameCommand extends Command {
    public GameCommand(ClimaxGames plugin) {
        super(plugin, "game", new CommandElement[]{
                GenericArguments.choices(Texts.of("action"), new HashMap<String, Object>() {{
                    put("start", null);
                    put("stop", null);
                }})
        });
    }

    @Override
    public CommandResult execute(Player player, CommandContext context) throws CommandException {
        String action = context.<String>getOne("action").get();

        if (action == null) {
            return CommandResult.empty();
        }

        if (action.equalsIgnoreCase("start")) {
            if (!plugin.getMinigame().getState().equals(Game.GameState.READY)) {
                throw new CommandException(Texts.builder("The game has already started.").style(TextStyles.BOLD).build());
            }

            plugin.getMinigame().setState(Game.GameState.STARTING);
        } else if (action.equalsIgnoreCase("stop")) {
            if (!plugin.getMinigame().getState().equals(Game.GameState.IN_GAME)) {
                throw new CommandException(Texts.builder("The game has not started yet.").style(TextStyles.BOLD).build());
            }

            plugin.getMinigame().setState(Game.GameState.ENDING);
        }

        return CommandResult.success();
    }
}
