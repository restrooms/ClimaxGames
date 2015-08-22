package net.climaxmc.game;

import lombok.Getter;
import net.climaxmc.ClimaxGames;
import net.climaxmc.core.utilities.*;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdown extends BukkitRunnable {
    @Getter
    private static boolean started = false;
    private Game game = ClimaxGames.getInstance().getManager().getGame();
    private int timer = 60;

    @Override
    public void run() {
        if (!game.getState().equals(Game.GameState.READY)) {
            started = false;
            cancel();
            return;
        }

        if (UtilPlayer.getAll().size() < game.getMinPlayers()) {
            started = false;

            UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, F.message("Countdown", "Not enough players to start the game.")));

            cancel();
            return;
        }

        if (timer <= 0) {
            started = false;

            game.setState(Game.GameState.PREPARE);

            cancel();
            return;
        }

        started = true;

        UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, F.message("Countdown", C.YELLOW + "The game will start in " + C.RED + timer + C.YELLOW + " seconds.")));

        timer--;
    }
}
