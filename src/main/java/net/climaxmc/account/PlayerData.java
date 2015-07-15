package net.climaxmc.account;

import net.climaxmc.utilities.Rank;
import org.spongepowered.api.entity.player.Player;

/**
 * Represents player data
 */
public class PlayerData {
    private Player player;
    private Rank rank;

    /**
     * Defines player data of the given player
     *
     * @param player Player to get data of
     */
    public PlayerData(Player player) {
        this(player, Rank.OWNER);
    }

    private PlayerData(Player player, Rank rank) {
        this.player = player;
        this.rank = rank;
    }

    /**
     * Gets the rank of the player
     *
     * @return Rank of player
     */
    public Rank getRank() {
        return rank;
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
}
