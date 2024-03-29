package net.climaxmc.kit;

import lombok.Data;
import net.climaxmc.ClimaxGames;
import net.climaxmc.core.ClimaxCore;
import net.climaxmc.core.mysql.*;
import net.climaxmc.core.utilities.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

/**
 * Represents a kit
 */
@Data
public abstract class Kit {
    protected static ClimaxGames plugin = ClimaxGames.getInstance();

    private String name;
    private String[] description;
    private int cost;
    private Ability[] abilities;
    private ItemStack itemInHand;

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param abilities   Abilities of kit
     */
    public Kit(String name, String[] description, Ability[] abilities, ItemStack itemInHand) {
        this(name, description, abilities, itemInHand, 0);
    }

    /**
     * Defines a kit
     *
     * @param name        Name of kit
     * @param description Description of kit
     * @param abilities   Abilities of kit
     * @param cost        Cost of kit
     */
    public Kit(String name, String[] description, Ability[] abilities, ItemStack itemInHand, int cost) {
        this.name = name;
        this.description = description;
        this.abilities = abilities;
        this.itemInHand = itemInHand;
        this.cost = cost;

        for (Ability ability : abilities) {
            plugin.getServer().getPluginManager().registerEvents(ability, plugin);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public void apply(Player player) {
        UtilPlayer.clearInventory(player);
        giveItems(player);
        plugin.getManager().getGame().getPlayerKits().put(player.getUniqueId(), this);
    }

    public abstract void giveItems(Player player);

    public Entity spawnEntity(Location location) {
        Villager villager = location.getWorld().spawn(location, Villager.class);
        villager.setRemoveWhenFarAway(false);
        villager.getEquipment().setItemInHand(itemInHand);
        villager.setAdult();
        villager.setAgeLock(true);
        villager.setProfession(Villager.Profession.LIBRARIAN);
        UtilEntity.removeAI(villager);
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setCustomName(C.GOLD + name + " Kit " + (cost == 0 ? "" : C.GREEN + cost + C.RED + C.BOLD + " C" + C.GOLD + C.BOLD + "Coins"));
        armorStand.setCustomNameVisible(true);
        return villager;
    }

    public void displayDescription(Player player) {
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

    /**
     * Checks if the player has the specified kit
     *
     * @param player Player to check
     */
    public boolean hasKit(Player player, GameType gameType) {
        PlayerData playerData = ClimaxCore.getPlayerData(player);
        boolean has = false;
        if (playerData.getKits().containsKey(gameType)) {
            for (String kitName : playerData.getKits().get(gameType)) {
                if (ChatColor.stripColor(kitName).equals(name)) {
                    has = true;
                }
            }
        }
        return cost == 0 || has;
    }

    /**
     * Purchases a kit
     *
     * @param player Player who purchased kit
     */
    public void purchaseKit(Player player, GameType gameType) {
        PlayerData playerData = ClimaxCore.getPlayerData(player);
        playerData.withdrawCoins(cost);
        if (!playerData.getKits().containsKey(gameType)) {
            playerData.getKits().put(gameType, new HashSet<>());
        }
        playerData.getKits().get(gameType).add(ChatColor.stripColor(name));
        ClimaxCore.getMySQL().executeUpdate(DataQueries.PURCHASE_KIT, playerData.getId(), gameType.getId(), ChatColor.stripColor(name));
    }
}
