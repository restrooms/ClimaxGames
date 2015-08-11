package net.climaxmc.mysql;

import lombok.Getter;
import net.climaxmc.command.commands.punishments.PunishType;
import net.climaxmc.command.commands.punishments.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.*;

public class MySQL {
    private final JavaPlugin plugin;
    private final String host;
    private final int port;
    private final String name;
    private final String username;
    private final String password;

    @Getter
    private Connection connection;

    /**
     * Defines a new MySQL connection
     *
     * @param plugin   Plugin that is loading MySQL
     * @param host     Host of the MySQL server
     * @param port     Port of the MySQL server
     * @param name     Name of the database
     * @param username Name of user with rights to the database
     * @param password Password of user with rights to the database
     */
    public MySQL(JavaPlugin plugin, String host, int port, String name, String username, String password) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.name = name;
        this.username = username;
        this.password = password;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name, username, password);
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not open MySQL connection! " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Could not close MySQL connection! " + e.getMessage());
            }

            connection = null;
        }
    }

    /**
     * Runs a MySQL query asynchronously
     *
     * @param runnable Runnable to run async
     */
    private void runAsync(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Executes a MySQL query
     *
     * @param query  The query to run on the database
     * @param values The values to insert into the query
     */
    public ResultSet executeQuery(String query, Object... values) {
        try {
            if (query.equals(AccountQueries.GET_PLAYERDATA_ID) || query.equals(AccountQueries.GET_PLAYERDATA_NAME) || query.equals(AccountQueries.GET_PLAYERDATA_UUID)) {
                Bukkit.broadcastMessage("Querying player data!");
            }
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name, username, password);
            }

            PreparedStatement statement = connection.prepareStatement(query);

            int i = 0;
            for (Object value : values) {
                statement.setObject(++i, value);
            }

            return statement.executeQuery();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not execute MySQL query! " + e.getMessage());
            return null;
        }
    }

    /**
     * Executes a MySQL update
     *
     * @param query The query to run on the database
     */
    public void executeUpdate(String query, Object... values) {
        runAsync(() -> {
            try {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name, username, password);
                }

                PreparedStatement statement = connection.prepareStatement(query);

                int i = 0;
                for (Object value : values) {
                    statement.setObject(++i, value);
                }

                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Could not execute MySQL query! " + e.getMessage());
            }
        });
    }

    /**
     * Create player data
     *
     * @param player Player to create data of
     */
    public void createPlayerData(Player player) {
        Bukkit.broadcastMessage("Creating player data!");
        executeUpdate(AccountQueries.CREATE_PLAYERDATA, player.getUniqueId().toString(), player.getName(), player.getAddress().getHostString());
    }

    /**
     * Get player data
     *
     * @param uuid UUID of the player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        ResultSet data = executeQuery(AccountQueries.GET_PLAYERDATA_UUID, uuid.toString());

        if (data == null) {
            return null;
        }

        PlayerData playerData = null;

        try {
            if (data.next()) {
                int id = data.getInt("playerid");
                String name = data.getString("name");
                String ip = data.getString("ip");
                Rank rank = Rank.valueOf(data.getString("rank"));
                int coins = data.getInt("coins");
                playerData = new PlayerData(this, id, uuid, name, ip, rank, coins, new ArrayList<>());

                ResultSet punishments = executeQuery(AccountQueries.GET_PUNISHMENTS, id);
                while (punishments != null && punishments.next()) {
                    PunishType type = PunishType.valueOf(punishments.getString("type"));
                    long time = punishments.getLong("time");
                    long expiration = punishments.getLong("expiration");
                    int punisherID = punishments.getInt("punisherid");
                    String reason = punishments.getString("reason");
                    playerData.getPunishments().add(new Punishment(id, type, time, expiration, punisherID, reason));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not get player data! " + e.getMessage());
        }

        return playerData;
    }

    /**
     * Get player data
     *
     * @param name Name of the player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(String name) {
        if (name == null) {
            return null;
        }

        ResultSet data = executeQuery(AccountQueries.GET_PLAYERDATA_NAME, name);

        if (data == null) {
            return null;
        }

        PlayerData playerData = null;

        try {
            if (data.next()) {
                int id = data.getInt("playerid");
                UUID uuid = UUID.fromString(data.getString("uuid"));
                String ip = data.getString("ip");
                Rank rank = Rank.valueOf(data.getString("rank"));
                int coins = data.getInt("coins");
                playerData = new PlayerData(this, id, uuid, name, ip, rank, coins, new ArrayList<>());

                ResultSet punishments = executeQuery(AccountQueries.GET_PUNISHMENTS, id);
                while (punishments != null && punishments.next()) {
                    PunishType type = PunishType.valueOf(punishments.getString("type"));
                    long time = punishments.getLong("time");
                    long expiration = punishments.getLong("expiration");
                    int punisherID = punishments.getInt("punisherid");
                    String reason = punishments.getString("reason");
                    playerData.getPunishments().add(new Punishment(id, type, time, expiration, punisherID, reason));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not get player data! " + e.getMessage());
        }

        return playerData;
    }

    /**
     * Get player data
     *
     * @param id ID of the player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(int id) {
        ResultSet data = executeQuery(AccountQueries.GET_PLAYERDATA_ID, id);

        if (data == null) {
            return null;
        }

        PlayerData playerData = null;

        try {
            if (data.next()) {
                UUID uuid = UUID.fromString(data.getString("uuid"));
                String name = data.getString("name");
                String ip = data.getString("ip");
                Rank rank = Rank.valueOf(data.getString("rank"));
                int coins = data.getInt("coins");
                playerData = new PlayerData(this, id, uuid, name, ip, rank, coins, new ArrayList<>());

                ResultSet punishments = executeQuery(AccountQueries.GET_PUNISHMENTS, id);
                while (punishments != null && punishments.next()) {
                    PunishType type = PunishType.valueOf(punishments.getString("type"));
                    long time = punishments.getLong("time");
                    long expiration = punishments.getLong("expiration");
                    int punisherID = punishments.getInt("punisherid");
                    String reason = punishments.getString("reason");
                    playerData.getPunishments().add(new Punishment(id, type, time, expiration, punisherID, reason));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not get player data! " + e.getMessage());
        }

        return playerData;
    }

    /**
     * Update Player Data
     *
     * @param column The column to update
     * @param to     What to update the column to
     * @param uuid   UUID of the player to update
     */
    public void updatePlayerData(String column, Object to, UUID uuid) {
        Bukkit.broadcastMessage("Updating player data!");
        executeUpdate("UPDATE `players` SET " + column + " = ? WHERE uuid = ?;", to, uuid.toString());
    }
}
