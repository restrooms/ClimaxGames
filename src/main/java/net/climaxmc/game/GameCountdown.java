package net.climaxmc.game;

import lombok.Getter;
import net.climaxmc.ClimaxGames;
import net.climaxmc.core.utilities.*;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdown extends BukkitRunnable {
    @Getter
    private static boolean started = false;
    private int timer = 45;

    @Override
    public void run() {
        Game game = ClimaxGames.getInstance().getManager().getGame();
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

        if (UtilPlayer.getAll().size() >= game.getMaxPlayers() && timer > 10) {
            timer = 10;
        }

        if (timer <= 0) {
            started = false;

            game.setState(Game.GameState.PREPARE);

            cancel();
            return;
        }

        if (timer == 1) {
            UtilPlayer.getAll().forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 3));
        } else if (timer <= 10) {
            UtilPlayer.getAll().forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1));
        }

        started = true;

        UtilPlayer.getAll().forEach(player -> UtilPlayer.sendActionBar(player, F.message("Countdown", C.YELLOW + "The game will start in " + C.RED + timer + C.YELLOW + " seconds.")));

        timer--;
    }
}
