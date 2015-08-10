package net.climaxmc.command.commands;

import net.climaxmc.command.Command;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.mysql.Rank;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.F;
import org.bukkit.entity.Player;

public class RankCommand extends Command {
    public RankCommand() {
        super(new String[]{"rank"}, Rank.OWNER, F.message("Administration", "/rank <player> <rank>"));
    }

    @Override
    public String execute(Player player, String[] args) {
        if (args.length != 2) {
            return usage;
        }

        PlayerData targetData = plugin.getMySQL().getPlayerData(args[0]);

        if (targetData == null) {
            return C.RED + "That player has never joined!";
        }

        Rank rank = Rank.fromString(args[1]);

        if (rank == null) {
            return C.RED + "That is not a valid rank!";
        }

        targetData.setRank(rank);

        return C.GREEN + targetData.getName() + "'s rank has been set to " + rank.toString() + "!";
    }
}
