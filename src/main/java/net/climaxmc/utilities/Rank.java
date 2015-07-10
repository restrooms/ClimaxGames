package net.climaxmc.utilities;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

/**
 * Represents a rank
 */
public enum Rank {
    OWNER("Owner", TextColors.RED, Integer.MAX_VALUE),
    DEVELOPER("Dev", TextColors.GOLD, 120),
    ADMINISTRATOR("Admin", TextColors.RED, 100),
    MODERATOR("Mod", TextColors.LIGHT_PURPLE, 80),
    BUILDER("Builder", TextColors.AQUA, 60),
    HELPER("Helper", TextColors.GREEN, 40),
    TRUSTED("Trusted", TextColors.DARK_AQUA, 20),
    DEFAULT(null, null, 0);

    private String prefix;
    private TextColor.Base color;
    private int permissionLevel;

    /**
     * Defines a rank
     *
     * @param prefix          Prefix of the rank
     * @param color           Color of the rank
     * @param permissionLevel Permission level of the rank
     */
    Rank(String prefix, TextColor.Base color, int permissionLevel) {
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
    public TextColor.Base getColor() {
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
