package net.climaxmc.managers;

import java.io.File;
import java.io.IOException;

public class GameWorldManager extends Manager {
    GameWorldManager() {
        initializeWorldFolders();
    }

    private void initializeWorldFolders() {
        File root = new File((System.getProperty("os.name").startsWith("Windows") ? "C:" : "") + File.separator + "maps");

        try {
            if (root.createNewFile()) {
                plugin.getLogger().info("Created maps folder at " + root.getPath());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not create ");
        }


    }
}
