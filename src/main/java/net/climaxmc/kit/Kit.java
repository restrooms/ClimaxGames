package net.climaxmc.kit;

import lombok.Data;
import net.climaxmc.ClimaxGames;
import net.climaxmc.utilities.*;
import org.bukkit.Location;
import org.bukkit.Sound;
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
    private ItemStack itemInHand;

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param perks       Perks of kit
     */
    public Kit(String name, String[] description, Perk[] perks, ItemStack itemInHand) {
        this(name, description, perks, itemInHand, 0);
    }

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param perks       Perks of kit
     * @param cost        Cost of kit
     */
    public Kit(String name, String[] description, Perk[] perks, ItemStack itemInHand, int cost) {
        this.name = name;
        this.description = description;
        this.perks = perks;
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

    public void apply(Player player) {
        UtilInv.clear(player);
        giveItems(player);
    }

    public abstract void giveItems(Player player);

    public Entity spawnEntity(Location location) {
        Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        zombie.setRemoveWhenFarAway(false);
        zombie.setCustomName(C.GOLD + name + " Kit" + (cost == 0 ? "" : C.GREEN + " $" + cost));
        zombie.setCustomNameVisible(false);
        zombie.getEquipment().setItemInHand(itemInHand);
        zombie.setBaby(false);
        zombie.setVillager(false);
        UtilEnt.removeAI(zombie);

        return zombie;
    }

    public void displayDescription(Player player) {
        for (int i = 0; i < 3; ++i) {
            player.sendMessage("");
        }

        player.sendMessage(F.line());
        player.sendMessage(C.GREEN + "Kit - " + C.WHITE + C.BOLD + name);

        player.sendMessage("");

        for (String descLine : description) {
            player.sendMessage(C.GRAY + descLine);
        }

        for (Perk perk : perks) {
            player.sendMessage("");
            player.sendMessage(C.WHITE + C.BOLD + perk.getName());
        }

        player.sendMessage(F.line());

        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 2, 3);
    }
}
