package net.climaxmc.utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class F {
    private static Text prefix(String prefix) {
        return Texts.builder(prefix + "> ").color(TextColors.BLUE).build();
    }

    public static Text message(String prefix, String message) {
        return prefix(prefix).builder().append(Texts.builder(message).color(TextColors.GRAY).build()).build();
    }

    public static Text denyPermissions(Rank rank) {
        return prefix("Permissions").builder().append(Texts.builder("This requires permissions rank [").color(TextColors.GRAY).build(), Texts.builder(rank.name()).color(TextColors.BLUE).build(), Texts.builder("].").color(TextColors.GRAY).build()).build();
    }
}
