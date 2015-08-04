package net.climaxmc.commands;

import net.climaxmc.WorldConfigGenerator;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class WorldConfigCommand implements CommandExecutor {
    private WorldConfigGenerator plugin;

    public WorldConfigCommand(WorldConfigGenerator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute that command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 3 || !args[0].equalsIgnoreCase("setspawn")) {
            player.sendMessage(ChatColor.RED + "/" + label + " setspawn <team> <spawn #>");
            return true;
        }

        String team = WordUtils.capitalizeFully(args[1]);
        int number = Integer.parseInt(args[2]);

        File file = new File(player.getWorld().getWorldFolder().getPath() + File.separator + "WorldConfig.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Could not create new configuration!");
            }
        }

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            player.sendMessage(ChatColor.RED + "Could not load existing configuration!");
        }

        if (config.getString("Name") == null) {
            config.set("Name", player.getWorld().getName());
        }

        if (config.getString("Author") == null) {
            config.set("Author", player.getName());
        }

        String path = "Spawns." + team + "." + number + ".";
        Location location = player.getLocation();

        config.set(path + "X", location.getX());
        config.set(path + "Y", location.getY());
        config.set(path + "Z", location.getZ());
        config.set(path + "Yaw", location.getYaw());
        config.set(path + "Pitch", location.getPitch());

        try {
            config.save(file);
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Could not save configuration!");
        }

        player.sendMessage(ChatColor.GREEN + "Spawn #" + number + " for team " + team + " was added successfully!");

        return true;
    }
}
