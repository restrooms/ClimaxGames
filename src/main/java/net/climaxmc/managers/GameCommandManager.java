package net.climaxmc.managers;

import com.google.common.collect.Sets;
import net.climaxmc.command.Command;
import net.climaxmc.command.commands.GameCommand;
import net.climaxmc.mysql.PlayerData;
import net.climaxmc.utilities.F;
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
                new GameCommand()
        );
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] args = message.substring(message.indexOf(' ') + 1).split(" ");
        String command = getFirstWord(message);
        for (Command possibleCommand : commands) {
            for (String name : possibleCommand.getNames()) {
                if (command.equalsIgnoreCase("/" + name)) {
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

                    event.setCancelled(true);
                }
            }
        }
    }


    private String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) {
            return text.substring(0, text.indexOf(' '));
        } else {
            return text;
        }
    }
}
