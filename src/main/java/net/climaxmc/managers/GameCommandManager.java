package net.climaxmc.managers;

import com.google.common.collect.Sets;
import net.climaxmc.account.PlayerData;
import net.climaxmc.command.Command;
import net.climaxmc.command.commands.GameCommand;
import net.climaxmc.utilities.F;
import net.climaxmc.utilities.Rank;
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
        for (Command command : commands) {
            for (String name : command.getNames()) {
                if (name.equalsIgnoreCase(event.getMessage())) {
                    Player player = event.getPlayer();
                    PlayerData playerData = new PlayerData(player);

                    if (!playerData.hasRank(Rank.ADMINISTRATOR)) {
                        player.sendMessage(F.denyPermissions(command.getRank()));
                    }

                    String result = command.execute(player, args);

                    if (result != null) {
                        player.sendMessage(result);
                    }

                    event.setCancelled(true);
                }
            }
        }
    }
}
