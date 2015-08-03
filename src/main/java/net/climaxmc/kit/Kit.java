package net.climaxmc.kit;

import lombok.Data;
import net.climaxmc.ClimaxGames;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.UtilEnt;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a kit
 */
@Data
public abstract class Kit {
    protected static ClimaxGames plugin = ClimaxGames.getInstance();

    private String name;
    private String[] description;
    private int cost;
    private Perk[] perks;
    private EntityType entityType;
    private ItemStack itemInHand;

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param perks       Perks of kit
     * @param entityType  Type of entity shown in game lobby
     */
    public Kit(String name, String[] description, Perk[] perks, EntityType entityType, ItemStack itemInHand) {
        this(name, description, perks, entityType, itemInHand, 0);
    }

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param perks       Perks of kit
     * @param entityType  Type of entity shown in game lobby
     * @param cost        Cost of kit
     */
    public Kit(String name, String[] description, Perk[] perks, EntityType entityType, ItemStack itemInHand, int cost) {
        this.name = name;
        this.description = description;
        this.perks = perks;
        this.entityType = entityType;
        this.itemInHand = itemInHand;
        this.cost = cost;

        for (Perk perk : perks) {
            ClimaxGames.getInstance().getServer().getPluginManager().registerEvents(perk, ClimaxGames.getInstance());
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract void apply(Player player);

    public Entity spawnEntity(Location location) {
        if (entityType == EntityType.PLAYER) {
            entityType = EntityType.ZOMBIE;
        }
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        entity.setRemoveWhenFarAway(false);
        entity.setCustomName(C.GOLD + name + " Kit" + (cost == 0 ? "" : C.GREEN + " $" + cost));
        entity.setCustomNameVisible(true);
        entity.getEquipment().setItemInHand(itemInHand);
        if (entityType == EntityType.SKELETON && name.contains("Wither")) {
            Skeleton skeleton = (Skeleton) entity;
            skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
        }
        UtilEnt.removeAI(entity);
        return entity;
    }
}
