package net.climaxmc.mysql;

public class AccountQueries {
    public static final String GET_PLAYERDATA = "SELECT * FROM `players` where `uuid` = ?;";
    public static final String CREATE_PLAYERDATA = "INSERT IGNORE INTO `players` (`uuid`, `name`) VALUES (?, ?);";
}
