package net.climaxmc.kit;

import java.awt.image.VolatileImage;

/**
 * Represents a kit
 */
public abstract class Kit {
    private String name;
    private String[] description;
    private int cost;
    private Perk[] perks;

    /**
     * Defines a kit
     *
     * @param name Name of kit
     * @param description Description of kit
     * @param perks Perks of kit
     */
    public Kit(String name, String[] description, Perk[] perks) {
        this.name = name;
        this.description = description;
        this.perks = perks;
        this.cost = 0;
    }

    /**
     * Defines a kit
     *
     * @param name Name of kit
     * @param description Description of kit
     * @param cost Cost of kit
     * @param perks Perks of kit
     */
    public Kit(String name, String[] description, Perk[] perks, int cost) {
        this(name, description, perks);
        this.cost = cost;
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

    public abstract void apply(/*Player player*/);
}
