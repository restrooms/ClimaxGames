package net.climaxmc.mysql;

public class AccountQueries {
    public static final String CREATE_PLAYERDATA = "INSERT IGNORE INTO `players` (`uuid`, `name`, `ip`) VALUES (?, ?, ?);";
    public static final String GET_PLAYERDATA_ID = "SELECT * FROM `players` WHERE `playerid` = ?;";
    public static final String GET_PLAYERDATA_UUID = "SELECT * FROM `players` WHERE `uuid` = ?;";
    public static final String GET_PLAYERDATA_NAME = "SELECT * FROM `players` WHERE `name` = ?;";

    public static final String CREATE_PUNISHMENT = "INSERT IGNORE INTO `punishments` (`playerid`, `type`, `time`, `expiration`, `punisherid`, `reason`) VALUES (?, ?, ?, ?, ?, ?);";
    public static final String GET_PUNISHMENTS = "SELECT * FROM `punishments` WHERE `playerid` = ?;";
    public static final String UPDATE_PUNISHMENT_TIME = "UPDATE `punishments` SET `expiration` = ? WHERE `playerid` = ? AND `type` = ? AND `time` = ?;";
}
