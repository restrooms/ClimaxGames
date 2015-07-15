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

public class GameCommandManager implements Manager {
    private GameManager manager;
    private Set<Command> commands;

    public GameCommandManager(GameManager manager) {
        this.manager = manager;

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

        if (!(source instanceof Player)) {
            source.sendMessage(Texts.of("You must be a player to execute that command."));
            return;
        }

        for (Command command : commands) {
            for (String name : command.getNames()) {
                if (name.equalsIgnoreCase(event.getCommand())) {
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
