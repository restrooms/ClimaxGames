package net.climaxmc.command.commands.punishments;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Punishment {
    private int playerID;
    private PunishType type;
    private long time;
    private long expiration;
    private int punisherID;
    private String reason;
}
