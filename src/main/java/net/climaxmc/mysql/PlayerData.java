package net.climaxmc.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.climaxmc.command.commands.punishments.Punishment;
import net.climaxmc.events.PlayerBalanceChangeEvent;
import net.climaxmc.game.GameType;
import net.climaxmc.kit.Kit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.*;

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
    private String server;
    private List<Punishment> punishments;
    private Map<GameType, Set<String>> kits;

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
        Bukkit.getServer().getPluginManager().callEvent(new PlayerBalanceChangeEvent(uuid, this.coins));
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
     * Sets the server that the player is currently on
     *
     * @param server Server that the player is currently on
     */
    public void setServer(String server) {
        mySQL.updatePlayerData("servername", this.server = server, uuid);
    }

    /**
     * Adds a punishment to the player
     *
     * @param punishment Punishment to apply
     */
    public void addPunishment(Punishment punishment) {
        punishments.add(punishment);
        mySQL.executeUpdate(DataQueries.CREATE_PUNISHMENT, id, punishment.getType().name(), punishment.getTime(), punishment.getExpiration(), punishment.getPunisherID(), punishment.getReason());
    }

    /**
     * Removes a punishment from the player
     *
     * @param punishment Punishment to remove
     */
    public void removePunishment(Punishment punishment) {
        punishments.remove(punishment);
        mySQL.executeUpdate(DataQueries.UPDATE_PUNISHMENT_TIME, 0, id, punishment.getType().name(), punishment.getTime());
    }

    /**
     * Checks if the player has the specified kit
     *
     * @param gameType Game type of kit
     * @param kit Kit to check
     */
    public boolean hasKit(GameType gameType, Kit kit) {
        boolean has = false;
        if (kits.containsKey(gameType)) {
            for (String kitName : kits.get(gameType)) {
                if (ChatColor.stripColor(kitName).equals(kit.getName())) {
                    has = true;
                }
            }
        }
        return kit.getCost() == 0 || has;
    }

    /**
     * Purchases a kit
     *
     * @param kit Kit to purchase
     */
    public void purchaseKit(GameType gameType, Kit kit) {
        withdrawCoins(kit.getCost());
        if (kits == null) {
            kits = new HashMap<>();
        }
        if (!kits.containsKey(gameType)) {
            kits.put(gameType, new HashSet<>());
        }
        kits.get(gameType).add(ChatColor.stripColor(kit.getName()));
        mySQL.executeUpdate(DataQueries.PURCHASE_KIT, id, gameType.getId(), ChatColor.stripColor(kit.getName()));
    }
}
