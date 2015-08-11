package net.climaxmc.command.commands.punishments;

import net.climaxmc.command.Command;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.mysql.Rank;
import net.climaxmc.utilities.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class UnMuteCommand extends Command {
    public UnMuteCommand() {
        super(new String[] {"unmute"}, Rank.HELPER, F.message("Punishments", "/unmute <player>"));
    }

    @Override
    public String execute(Player player, String[] args) {
        if (args.length != 1) {
            return usage;
        }

        PlayerData targetData = plugin.getPlayerData(args[0]);

        if (targetData == null) {
            return F.message("Punishments", "That player has never joined!");
        }

        if (targetData.getPunishments() != null && targetData.getPunishments().size() != 0) {
            Set<Punishment> remove = new HashSet<>();
            targetData.getPunishments().stream()
                    .filter(punishment -> punishment.getExpiration() == -1 || System.currentTimeMillis() <= (punishment.getTime() + punishment.getExpiration()))
                    .filter(punishment -> punishment.getType().equals(PunishType.MUTE))
                    .forEach(remove::add);
            if (remove.size() == 0) {
                return F.message("Punishments", "That player is not muted!");
            }
            remove.forEach(targetData::removePunishment);
            UtilPlayer.getAll(Rank.HELPER).forEach(staff -> staff.sendMessage(F.message("Punishments", C.RED + player.getName() + " unmuted " + targetData.getName() + ".")));
            Player target = plugin.getServer().getPlayer(targetData.getUuid());
            if (target != null) {
                target.sendMessage(F.message("Punishments", C.GREEN + "You were unmuted by " + player.getName() + "."));
            }
        } else {
            return F.message("Punishments", "That player is not muted!");
        }

        return null;
    }
}
