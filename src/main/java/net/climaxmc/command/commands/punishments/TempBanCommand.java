package net.climaxmc.command.commands.punishments;

import net.climaxmc.command.Command;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.mysql.Rank;
import net.climaxmc.utilities.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TempBanCommand extends Command {
    public TempBanCommand() {
        super(new String[] {"tempban"}, Rank.MODERATOR, F.message("Punishments", "/tempban <player> <time (d/h/m)> <reason>"));
    }

    @Override
    public String execute(Player player, String[] args) {
        if (args.length < 3) {
            return usage;
        }

        PlayerData playerData = plugin.getPlayerData(player);
        PlayerData targetData = plugin.getMySQL().getPlayerData(args[0]);

        if (targetData == null) {
            return F.message("Punishments", "That player has never joined!");
        }

        long time = 0;

        String reason = "";
        for (int i = 2; i < args.length; i++) {
            reason += args[i] + " ";
        }
        reason = reason.trim();

        final String finalReason = reason;
        targetData.addPunishment(new Punishment(targetData.getId(), PunishType.BAN, -1, playerData.getId(), reason));
        UtilPlayer.getAll(Rank.HELPER).forEach(staff -> player.sendMessage(F.message("Punishments", C.RED + player.getName() + " has permanently banned " + targetData.getName() + " for " + Time.toReadableString(time) + " for " + finalReason + ".")));

        OfflinePlayer target = plugin.getServer().getPlayer(targetData.getUuid());
        if (target.isOnline()) {
            target.getPlayer().kickPlayer(F.message("Punishments", C.RED + "You were temporarily banned by " + player.getName() + " for " + Time.toReadableString(time) + " for " + reason + "."));
        }

        return null;
    }
}
