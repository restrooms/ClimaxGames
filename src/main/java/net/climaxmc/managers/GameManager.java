package net.climaxmc.managers;

import com.google.common.collect.Sets;
import lombok.*;
import net.climaxmc.game.Game;
import net.climaxmc.utilities.C;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class GameManager extends Manager {
    private Set<Manager> managers;
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private Game game = null;

    @Getter
    private List<Location> lobbyKitEntityLocations = Arrays.asList(
            new Location(plugin.getServer().getWorld("world"), 88.5, 69, 86.5, -45, 0),
            new Location(plugin.getServer().getWorld("world"), 96.5, 69, 86.5, 45, 0),
            new Location(plugin.getServer().getWorld("world"), 96.5, 69, 94.5, 135, 0),
            new Location(plugin.getServer().getWorld("world"), 88.5, 69, 94.5, -135, 0)
    );

    public GameManager() {
        manager = this;

        managers = Sets.newHashSet(this,
                new GameConfigurationManager(),
                new GameWorldManager(),
                new GameLobbyManager(),
                new GameCommandManager(),
                new GamePlayerManager()
        );

        managers.forEach(manager -> plugin.getServer().getPluginManager().registerEvents(manager, plugin));

        game.setState(Game.GameState.READY);
    }

    protected void initializeLobbyScoreboard(Player player) {
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("GameLobby", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(C.RED + C.BOLD + "Climax" + C.GOLD + C.BOLD + "MC");
        scoreboard.registerNewTeam("LobbyTeam");
        objective.getScore(C.GREEN + C.BOLD + "Kit").setScore(9);
        objective.getScore(game.getPlayerKits().get(player.getUniqueId()).getName()).setScore(8);
        player.setScoreboard(scoreboard);
    }
}
