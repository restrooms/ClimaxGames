package net.climaxmc.managers;

import com.google.common.collect.Sets;
import net.climaxmc.account.PlayerData;
import net.climaxmc.command.Command;
import net.climaxmc.command.commands.GameCommand;
import net.climaxmc.utilities.F;
import net.climaxmc.utilities.Rank;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

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

    @Subscribe
    public void onCommand(CommandEvent event) {
        CommandSource source = event.getSource();

        for (Command command : commands) {
            for (String name : command.getNames()) {
                if (name.equalsIgnoreCase(event.getCommand())) {
                    if (!(source instanceof Player)) {
                        source.sendMessage(Texts.of("You must be a player to execute that command."));
                        event.setResult(CommandResult.success());
                        event.setCancelled(true);
                        return;
                    }

                    Player player = (Player) source;
                    PlayerData playerData = new PlayerData(player);

                    if (!playerData.hasRank(Rank.ADMINISTRATOR)) {
                        player.sendMessage(F.denyPermissions(command.getRank()));
                    }

                    Text result = command.execute(player, event.getArguments().split(" "));

                    if (result != null) {
                        player.sendMessage(result);
                    }

                    event.setResult(CommandResult.success());
                    event.setCancelled(true);
                }
            }
        }
    }
}
