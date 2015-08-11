package net.climaxmc.managers;

import com.google.common.collect.Sets;
import net.climaxmc.command.Command;
import net.climaxmc.command.commands.GameCommand;
import net.climaxmc.command.commands.RankCommand;
import net.climaxmc.command.commands.punishments.*;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.utilities.F;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Set;

public class GameCommandManager extends Manager {
    private Set<Command> commands;

    GameCommandManager() {
        initializeCommands();
    }

    private void initializeCommands() {
        commands = Sets.newHashSet(
                new GameCommand(),
                new RankCommand(),
                new BanCommand(),
                new TempBanCommand(),
                new MuteCommand(),
                new TempMuteCommand(),
                new KickCommand(),
                new WarnCommand(),
                new UnBanCommand(),
                new UnMuteCommand()
        );
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] args = new String[0];
        if (message.contains(" ")) {
            args = StringUtils.substringAfter(message, " ").split(" ");
            message = message.split(" ")[0];
        }
        for (Command possibleCommand : commands) {
            for (String name : possibleCommand.getNames()) {
                if (message.equalsIgnoreCase("/" + name)) {
                    event.setCancelled(true);

                    Player player = event.getPlayer();
                    PlayerData playerData = plugin.getPlayerData(player);

                    if (playerData.hasRank(possibleCommand.getRank())) {
                        String result = possibleCommand.execute(player, args);

                        if (result != null) {
                            player.sendMessage(result);
                        }
                    } else {
                        player.sendMessage(F.denyPermissions(possibleCommand.getRank()));
                    }
                }
            }
        }
    }
}
