package net.climaxmc.game.games.paintball;

import com.darkblade12.particleeffect.ParticleEffect;
import net.climaxmc.ClimaxGames;
import net.climaxmc.core.utilities.UtilParticle;
import net.climaxmc.core.utilities.UtilPlayer;
import net.climaxmc.game.GameTeam;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TNTParticle extends BukkitRunnable {
    private UtilParticle.ParticleType particleType;
    private double count = 0.0D;
    private Entity entity;
    private GameTeam team;

    public TNTParticle(UtilParticle.ParticleType particleType, TNTPrimed tnt, GameTeam team) {
        this.particleType = particleType;
        this.entity = tnt;
        this.team = team;
        runTaskTimer(ClimaxGames.getInstance(), 2, 2);
    }

    @Override
    public void run() {
        if (entity.isValid()) {
            Location location = entity.getLocation();

            double count2 = this.count;
            double hauteure2 = -1.0D;
            double rayon2 = 1.5D;

            List<Location> points2 = new ArrayList<>();
            ArrayList<Location> points = new ArrayList<>();
            for (; ; ) {
                double nombre2 = 3.141592653589793D + count2 * 3.141592653589793D / 7.0D;
                Location loc2 = new Location(entity.getWorld(), location.getX() +
                        Math.cos(nombre2 * 0.6) *
                                rayon2, location.getY() +
                        hauteure2, location.getZ() +
                        Math.sin(nombre2 * 0.6) *
                                rayon2);
                Location loc3 = new Location(entity.getWorld(), location.getX() +
                        Math.cos(nombre2 * 0.6) *
                                -rayon2, location.getY() +
                        hauteure2, location.getZ() +
                        Math.sin(nombre2 * 0.6) *
                                -rayon2);

                points2.add(loc2);
                points.add(loc3);
                if (count2 >= 36.0D + count) {
                    break;
                }
                rayon2 -= 0.04D;
                hauteure2 += 0.11D;
                count2 += 1.0D;
            }
            for (Location l4 : points2) {
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(team.getColor()), l4, UtilPlayer.getAll());
                //new UtilParticle(particleType, 0.0D, 1, 0.1000000014901161D).sendToLocation(l4);
            }
            for (Location l3 : points) {
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(team.getColor()), l3, UtilPlayer.getAll());
                //new UtilParticle(particleType, 0.0D, 1, 0.1000000014901161D).sendToLocation(l3);
            }
        } else {
            cancel();
        }
        if (count >= 30.0D) {
            count = 0.0D;
        }
        count += 0.2;
    }
}
