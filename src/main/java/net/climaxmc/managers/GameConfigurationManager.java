package net.climaxmc.managers;

import net.climaxmc.core.mysql.GameType;
import net.climaxmc.game.Game;
import net.climaxmc.game.games.paintball.Paintball;

import java.io.*;

public class GameConfigurationManager extends Manager {
    private boolean gameEnabled = false;

    GameConfigurationManager() {
        initializeConfiguration();
    }

    private void initializeConfiguration() {
        try {
            File file = new File("GameSettings.config");

            if (!file.exists()) {
                createConfiguration();
            }

            FileInputStream fstream = new FileInputStream("GameSettings.config");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            br.lines().forEach(line -> {
                String[] tokens = line.split("=");

                if (tokens.length >= 2) {
                    GameType type = GameType.valueOf(tokens[0]);
                    boolean enabled = Boolean.valueOf(tokens[1]);

                    if (enabled) {
                        manager.setGame(getGameFromType(type));
                        gameEnabled = true;
                    }
                }
            });

            in.close();

            if (!gameEnabled) {
                plugin.getLogger().warning("No game enabled in configuration! Shutting down server!");
                System.exit(0);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not initialize configuration!");
        }
    }

    private void createConfiguration() {
        try {
            FileWriter fstream = new FileWriter("GameSettings.config");
            BufferedWriter out = new BufferedWriter(fstream);

            for (GameType type : GameType.values()) {
                out.write(type.name() + "=" + type.isEnabled() + "\n");
            }

            out.close();
        } catch (IOException e) {
            plugin.getLogger().severe("Could not create/save game settings configuration!");
        }
    }

    private Game getGameFromType(GameType type) {
        if (type == GameType.PAINTBALL) {
            return new Paintball();
        }
        return null;
    }
}
