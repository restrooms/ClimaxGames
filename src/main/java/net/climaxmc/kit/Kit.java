package net.climaxmc.kit;

import net.climaxmc.ClimaxGames;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStackBuilder;

/**
 * Represents a kit
 */
public abstract class Kit {
    protected static ClimaxGames plugin = ClimaxGames.getInstance();
    protected static ItemStackBuilder itemBuilder = plugin.getGame().getRegistry().getItemBuilder();

    private String name;
    private String[] description;
    private int cost;
    private Perk[] perks;

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param perks       Perks of kit
     */
    public Kit(String name, String[] description, Perk[] perks) {
        this(name, description, perks, 0);
    }

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param cost        Cost of kit
     * @param perks       Perks of kit
     */
    public Kit(String name, String[] description, Perk[] perks, int cost) {
        this.name = name;
        this.description = description;
        this.perks = perks;
        this.cost = cost;

        for (Perk perk : perks) {
            ClimaxGames.getInstance().getGame().getEventManager().register(ClimaxGames.getInstance(), perk);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the name of the kit
     *
     * @return Name of kit
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the kit
     *
     * @return Description of kit
     */
    public String[] getDescription() {
        return description;
    }

    /**
     * Gets the perks of the kit
     *
     * @return Perks of kit
     */
    public Perk[] getPerks() {
        return perks;
    }

    /**
     * Gets the cost of the kit
     *
     * @return Cost of kit
     */
    public int getCost() {
        return cost;
    }

    public abstract void apply(Player player);
}
