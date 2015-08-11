package net.climaxmc.command.commands.punishments;

import net.climaxmc.command.Command;
import net.climaxmc.mysql.*;
import net.climaxmc.utilities.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BanCommand extends Command {
    public BanCommand() {
        super(new String[] {"ban"}, Rank.MODERATOR, F.message("Punishments", "/ban <player> <reason>"));
    }

    @Override
    public String execute(Player player, String[] args) {
        if (args.length < 2) {
            return usage;
        }

        PlayerData playerData = plugin.getPlayerData(player);
        PlayerData targetData = plugin.getPlayerData(args[0]);

        if (targetData == null) {
            return F.message("Punishments", "That player has never joined!");
        }

        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason += args[i] + " ";
        }
        reason = reason.trim();
        final String finalReason = reason;

        targetData.addPunishment(new Punishment(targetData.getId(), PunishType.BAN, System.currentTimeMillis(), -1, playerData.getId(), reason));
        UtilPlayer.getAll(Rank.HELPER).forEach(staff -> player.sendMessage(F.message("Punishments", C.RED + player.getName() + " has permanently banned " + targetData.getName() + " for " + finalReason + ".")));

        OfflinePlayer target = plugin.getServer().getPlayer(targetData.getUuid());
        if (target.isOnline()) {
            target.getPlayer().kickPlayer(F.message("Punishments", C.RED + "You were permanently banned by " + player.getName() + " for " + reason + ".\n"
                    + "Appeal on forum.climaxmc.net if you believe that this is in error!"));
        }

        return null;
    }
}
