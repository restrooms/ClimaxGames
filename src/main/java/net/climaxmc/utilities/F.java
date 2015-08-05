package net.climaxmc.utilities;

public class F {
    private static String prefix(String prefix) {
        return C.GOLD + C.BOLD + prefix + C.WHITE + "\u00bb ";
    }

    public static String message(String prefix, String message) {
        return prefix(prefix) + C.GRAY + message;
    }

    public static String denyPermissions(Rank rank) {
        return prefix("Permissions") + C.GRAY + "This requires permissions rank [" + C.BLUE + rank.name() + C.GRAY + "].";
    }

    public static String line() {
        return C.DARK_GREEN + C.STRIKETHROUGH + "=============================================";
    }
}
