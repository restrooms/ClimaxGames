package net.climaxmc.game;

import lombok.*;
import net.climaxmc.game.games.paintball.Paintball;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum GameType {
    PAINTBALL(1, Paintball.class, "Paintball", "PB");

    private final int id;
    private final Class<? extends Game> game;
    private final String name;
    private final String prefix;
    @Setter
    private boolean enabled;

    public static GameType fromID(int id) {
        for (GameType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return null;
    }
}
