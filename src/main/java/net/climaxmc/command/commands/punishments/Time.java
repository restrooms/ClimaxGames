package net.climaxmc.command.commands.punishments;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum Time {
    MINUTES(1000 * 60, 'm'),
    HOURS(MINUTES.milliseconds * 60, 'h'),
    DAYS(HOURS.milliseconds * 24, 'd');

    @Getter
    private final long milliseconds;
    private final char id;

    public static Time fromId(char id) {
        for (Time time : values()) {
            if (id == time.id) {
                return time;
            }
        }
        return null;
    }

    public static String toReadableString(long milliseconds) {
        long minutes = (milliseconds / MINUTES.milliseconds) % 60;
        long hours = (milliseconds / HOURS.milliseconds) % 24;
        long days = (milliseconds / DAYS.milliseconds) % 24;

        if (days <= 0) {
            if (hours <= 0) {
                return minutes + " minutes";
            }
            return hours + " hours and " + minutes + " minutes";
        }
        return days + " days, " + hours + " hours, and " + minutes + " minutes";
    }
}
