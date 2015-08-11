package net.climaxmc.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.climaxmc.command.commands.punishments.Punishment;

import java.util.List;
import java.util.UUID;

/**
 * Represents player data
 */
@Data
@AllArgsConstructor
public class PlayerData {
    private final MySQL mySQL;
    private final int id;
    private final UUID uuid;
    private String name;
    private String ip;
    private Rank rank;
    private int coins;

    private List<Punishment> punishments;

    /**
     * Sets the player's name
     *
     * @param name Name of the player
     */
    public void setName(String name) {
        mySQL.updatePlayerData("name", this.name = name, uuid);
    }

    /**
     * Sets the player's IP
     *
     * @param ip IP of the player
     */
    public void setIP(String ip) {
        mySQL.updatePlayerData("ip", this.ip = ip, uuid);
    }

    /**
     * Sets the player's rank
     *
     * @param rank Rank of the player
     */
    public void setRank(Rank rank) {
        mySQL.updatePlayerData("rank", (this.rank = rank).name(), uuid);
    }

    /**
     * Sets the player's coins
     *
     * @param coins Coins of the player
     */
    public void setCoins(int coins) {
        mySQL.updatePlayerData("coins", this.coins = coins, uuid);
    }

    /**
     * Deposits coins to the player's coin balance
     *
     * @param coins Amount of coins to deposit
     */
    public void depositCoins(int coins) {
        setCoins(this.coins += coins);
    }

    /**
     * Withdraws coins to the player's coin balance
     *
     * @param coins Amount of coins to withdraw
     */
    public void withdrawCoins(int coins) {
        setCoins(this.coins -= coins);
    }

    /**
     * Checks if the player has the specified rank
     *
     * @param rank Rank to compare
     * @return if the player has specified rank
     */
    public boolean hasRank(Rank rank) {
        return this.rank.getPermissionLevel() >= rank.getPermissionLevel();
    }

    /**
     * Adds a punishment to the player
     *
     * @param punishment Punishment to apply
     */
    public void addPunishment(Punishment punishment) {
        punishments.add(punishment);
        mySQL.executeUpdate(AccountQueries.CREATE_PUNISHMENT, id, punishment.getType().name(), punishment.getTime(), punishment.getExpiration(), punishment.getPunisherID(), punishment.getReason());
    }

    /**
     * Removes a punishment from the player
     *
     * @param punishment Punishment to remove
     */
    public void removePunishment(Punishment punishment) {
        punishments.remove(punishment);
        mySQL.executeUpdate(AccountQueries.UPDATE_PUNISHMENT_TIME, 0, id, punishment.getType().name(), punishment.getTime());
    }
}
