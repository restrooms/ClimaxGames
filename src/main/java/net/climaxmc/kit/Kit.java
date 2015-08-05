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
        Villager villager = location.getWorld().spawn(location, Villager.class);
        villager.setRemoveWhenFarAway(false);
        //villager.setCustomName(C.GOLD + name + " Kit" + (cost == 0 ? "" : C.GREEN + " $" + cost));
        //villager.setCustomNameVisible(true);
        villager.getEquipment().setItemInHand(itemInHand);
        villager.setAdult();
        villager.setAgeLock(true);
        villager.setProfession(Villager.Profession.LIBRARIAN);
        UtilEnt.removeAI(villager);
        ArmorStand armorStand = location.getWorld().spawn(location/*.multiply(0.9999999999999999).add(0, 0.4, 0)*/, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setCustomName(C.GOLD + name + " Kit" + (cost == 0 ? "" : C.GREEN + " $" + cost));
        armorStand.setCustomNameVisible(true);
        return villager;
    }

    public void displayDescription(Player player) {
        for (int i = 0; i < 3; ++i) {
            player.sendMessage("");
        }

        player.sendMessage(F.topLine());
        player.sendMessage(C.GOLD + C.BOLD + "Kit " + C.WHITE + "\u00bb " + C.WHITE + name);

        player.sendMessage("");

        for (String descLine : description) {
            player.sendMessage(C.GRAY + descLine);
        }

        player.sendMessage("");

        player.sendMessage(F.bottomLine());

        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 2, 3);
    }
}
