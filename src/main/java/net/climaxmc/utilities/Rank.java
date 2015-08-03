package net.climaxmc.utilities;

/**
 * Represents a rank
 */
public enum Rank {
    OWNER("Owner", C.RED, Integer.MAX_VALUE),
    DEVELOPER("Dev", C.GOLD, 120),
    ADMINISTRATOR("Admin", C.RED, 100),
    MODERATOR("Mod", C.LIGHT_PURPLE, 80),
    BUILDER("Builder", C.AQUA, 60),
    HELPER("Helper", C.GREEN, 40),
    TRUSTED("Trusted", C.DARK_AQUA, 20),
    DEFAULT(null, null, 0);

    private String prefix;
    private String color;
    private int permissionLevel;

    /**
     * Defines a rank
     *
     * @param prefix          Prefix of the rank
     * @param color           Color of the rank
     * @param permissionLevel Permission level of the rank
     */
    Rank(String prefix, String color, int permissionLevel) {
        this.prefix = prefix;
        this.color = color;
        this.permissionLevel = permissionLevel;
    }

    /**
     * Gets the prefix of the rank
     *
     * @return Prefix of rank
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the color of the rank
     *
     * @return Color of rank
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the permission level of the rank
     *
     * @return Permission level of rank
     */
    public int getPermissionLevel() {
        return permissionLevel;
    }
}
