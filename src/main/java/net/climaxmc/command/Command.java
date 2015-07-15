package net.climaxmc.command;

import net.climaxmc.ClimaxGames;
import net.climaxmc.utilities.Rank;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Represents a command
 */
public abstract class Command {
    protected ClimaxGames plugin = ClimaxGames.getInstance();
    protected String[] names;
    protected Rank rank;
    protected Text usage;

    /**
     * Defines a command
     *
     * @param names  Names of command (includes aliases)
     */
    public Command(String[] names, Text usage) {
        this(names, Rank.DEFAULT, usage);
    }

    /**
     * Defines a command
     *
     * @param names  Names of command (includes aliases)
     * @param rank   Rank of command
     */
    public Command(String[] names, Rank rank, Text usage) {
        this.names = names;
        this.rank = rank;
        this.usage = usage;
    }

    /**
     * Executes the command
     *
     * @param player Player that executed command
     * @param args Arguments of command
     * @return Result of execution
     */
    public abstract Text execute(Player player, String[] args);

    /**
     * Get the command names
     *
     * @return Command names
     */
    public String[] getNames() {
        return names;
    }

    /**
     * Get the command rank
     *
     * @return Command rank
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Gets the command usage
     *
     * @return Command usage
     */
    public Text getUsage() {
        return usage;
    }
}
