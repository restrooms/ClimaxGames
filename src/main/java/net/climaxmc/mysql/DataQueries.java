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

    public static final String CREATE_SERVER = "INSERT IGNORE INTO `servers` (`globalid`, `gameid`, `serverid`, `ip`, `port`, `players`) VALUES (" +
            "(SELECT MIN(`t1`.`globalid` + 1) AS nextID FROM `servers` `t1` LEFT JOIN `servers` `t2` ON `t1`.`globalid` + 1 = `t2`.`globalid` WHERE `t2`.`globalid` IS NULL), ?, " +
            "(SELECT MIN(`t1`.`serverid` + 1) AS nextID FROM `servers` `t1` LEFT JOIN `servers` `t2` ON `t1`.`serverid` + 1 = `t2`.`serverid` WHERE `t2`.`serverid` IS NULL), ?, ?, ?);";
    public static final String GET_SERVER_ID = "SELECT `globalid` FROM `servers` WHERE `ip` = ? AND `port` = ?;";
    public static final String UPDATE_PLAYERS_ONLINE = "UPDATE `servers` SET `players` = ? WHERE `globalid` = ?;";
    public static final String DELETE_SERVER = "DELETE FROM `servers` WHERE `globalid` = ?;";
}
