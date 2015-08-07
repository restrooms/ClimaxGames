package net.climaxmc.managers;

import com.google.common.collect.Sets;
import lombok.*;
import net.climaxmc.game.Game;
import net.climaxmc.game.GameTeam;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.UtilPlayer;
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
        objective.setDisplayName(C.RED + C.BOLD + "Climax" + C.WHITE + C.BOLD + "Games");
        scoreboard.registerNewTeam("LobbyTeam");
        objective.getScore("").setScore(9);
        objective.getScore(C.RED + C.BOLD + "Players" + C.WHITE + " \u00bb " + C.YELLOW + UtilPlayer.getAll().size() + "/" + manager.getGame().getMaxPlayers()).setScore(8);
        objective.getScore(" ").setScore(7);
        objective.getScore(C.GOLD + C.BOLD + "Money" + C.WHITE + " \u00bb " + C.YELLOW + "$" + 0).setScore(6);
        objective.getScore("  ").setScore(5);
        objective.getScore(C.RED + C.BOLD + "Kit" + C.WHITE + " \u00bb " + game.getPlayerKits().get(player.getUniqueId()).getName()).setScore(4);
        objective.getScore("   ").setScore(3);
        objective.getScore(C.GOLD + C.BOLD + "Map" + C.WHITE + " \u00bb " + C.YELLOW + game.getWorldConfig().getMapName()).setScore(2);
        objective.getScore("    ").setScore(1);
        GameTeam team = game.getPlayerTeam(player);
        objective.getScore(C.RED + C.BOLD + "Team" + C.WHITE + " \u00bb " + team.getColorCode() + team.getName()).setScore(0);
        player.setScoreboard(scoreboard);
    }
}
