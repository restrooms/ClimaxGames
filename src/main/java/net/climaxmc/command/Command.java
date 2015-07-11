package net.climaxmc.command;

import net.climaxmc.ClimaxGames;
import net.climaxmc.utilities.F;
import net.climaxmc.utilities.PlayerData;
import net.climaxmc.utilities.Rank;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.args.CommandElement;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

/**
 * Represents a command
 */
public abstract class Command implements CommandExecutor {
    protected CommandSpec commandSpec = CommandSpec.builder().executor(this).build();
    protected ClimaxGames plugin;
    private String[] names;
    private Rank rank;

    /**
     * Defines a command
     *
     * @param plugin Main plugin class
     * @param name   Name of command
     */
    public Command(ClimaxGames plugin, String name) {
        this(plugin, name, Rank.DEFAULT);
    }

    /**
     * Defines a command
     *
     * @param plugin Main plugin class
     * @param name   Name of command
     */
    public Command(ClimaxGames plugin, String name, CommandElement[] args) {
        this(plugin, name, Rank.DEFAULT);
    }

    /**
     * Defines a command
     *
     * @param plugin Main plugin class
     * @param names  Names of command (includes aliases)
     */
    public Command(ClimaxGames plugin, String[] names) {
        this(plugin, names, Rank.DEFAULT);
    }

    /**
     * Defines a command
     *
     * @param plugin Main plugin class
     * @param name   Name of command
     * @param rank   Rank of command
     */
    public Command(ClimaxGames plugin, String name, Rank rank) {
        this(plugin, new String[]{name}, rank);
    }

    /**
     * Defines a command
     *
     * @param plugin Main plugin class
     * @param names  Names of command (includes aliases)
     * @param rank   Rank of command
     */
    public Command(ClimaxGames plugin, String[] names, Rank rank) {
        this.plugin = plugin;
        this.names = names;
        this.rank = rank;

        plugin.getGame().getCommandDispatcher().register(plugin, commandSpec, names);
    }

    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        if (source instanceof Player) {
            Player player = (Player) source;
            PlayerData playerData = new PlayerData(player);

            if (!playerData.hasRank(Rank.ADMINISTRATOR)) {
                throw new CommandException(F.denyPermissions(rank));
            }

            execute(player, context);
        } else {
            throw new CommandException(Texts.of("You must be a player to execute that command."));
        }

        return CommandResult.success();
    }

    public abstract void execute(Player player, CommandContext context) throws CommandException;
}
