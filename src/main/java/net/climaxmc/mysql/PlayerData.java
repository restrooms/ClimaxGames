package net.climaxmc.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Represents player data
 */
@Data
@AllArgsConstructor
public class PlayerData {
    private MySQL mySQL;
    private UUID uuid;
    private String name;
    private String ip;
    private Rank rank;
    private int coins;

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
