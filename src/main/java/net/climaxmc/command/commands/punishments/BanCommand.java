package net.climaxmc.command.commands.punishments;

import net.climaxmc.command.Command;
import net.climaxmc.mysql.*;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.F;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BanCommand extends Command {
    public BanCommand() {
        super(new String[] {"ban"}, Rank.MODERATOR, F.message("Punishments", "/ban <player> <reason>"));
    }

    @Override
    public String execute(Player player, String[] args) {
        if (args.length != 2) {
            return usage;
        }

        PlayerData playerData = plugin.getPlayerData(player);
        PlayerData targetData = plugin.getMySQL().getPlayerData(args[0]);

        if (targetData == null) {
            return F.message("Punishments", "That player has never joined!");
        }

        targetData.addPunishment(new Punishment(targetData.getId(), PunishType.BAN, -1, playerData.getId(), args[1]));
        plugin.getServer().broadcastMessage(F.message("Punishments", C.RED + player.getName() + " has permanently banned " + targetData.getName() + "."));

        OfflinePlayer target = plugin.getServer().getPlayer(targetData.getUuid());
        if (target.isOnline()) {
            target.getPlayer().kickPlayer(F.message("Punishments", C.RED + "You were permanently banned by " + player.getName() + " for " + args[1] + "."));
        }

        return null;
    }
}
