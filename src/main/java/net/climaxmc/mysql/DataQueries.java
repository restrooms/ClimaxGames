package net.climaxmc.mysql;

public class DataQueries {
    public static final String CREATE_PLAYERDATA = "INSERT IGNORE INTO `players` (`uuid`, `name`, `ip`) VALUES (?, ?, ?);";
    public static final String GET_PLAYERDATA_ID = "SELECT * FROM `players` WHERE `playerid` = ?;";
    public static final String GET_PLAYERDATA_UUID = "SELECT * FROM `players` WHERE `uuid` = ?;";
    public static final String GET_PLAYERDATA_NAME = "SELECT * FROM `players` WHERE `name` = ?;";
    public static final String GET_PLAYER_UUID_FROM_IP = "SELECT `uuid` FROM `players` WHERE `ip` = ?;";

    public static final String CREATE_PUNISHMENT = "INSERT IGNORE INTO `punishments` (`playerid`, `type`, `time`, `expiration`, `punisherid`, `reason`) VALUES (?, ?, ?, ?, ?, ?);";
    public static final String GET_PUNISHMENTS = "SELECT * FROM `punishments` WHERE `playerid` = ?;";
    public static final String UPDATE_PUNISHMENT_TIME = "UPDATE `punishments` SET `expiration` = ? WHERE `playerid` = ? AND `type` = ? AND `time` = ?;";

    public static final String PURCHASE_KIT = "INSERT IGNORE INTO `kitpurchases` (`playerid`, `gameid`, `kitname`) VALUES (?, ?, ?);";
    public static final String GET_PURCHASED_KITS = "SELECT * FROM `kitpurchases` WHERE `playerid` = ?;";

}
