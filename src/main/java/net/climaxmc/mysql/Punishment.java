package net.climaxmc.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.climaxmc.command.commands.punishments.PunishType;

@Data
@AllArgsConstructor
public class Punishment {
    private int playerID;
    private PunishType type;
    private long time;
    private long expireTime;
}
