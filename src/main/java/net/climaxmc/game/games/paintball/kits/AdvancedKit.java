package net.climaxmc.game.games.paintball.kits;

import net.climaxmc.ClimaxGames;
import net.climaxmc.kit.Kit;
import net.climaxmc.kit.Ability;
import net.climaxmc.kit.abilities.AdvancedGunAbility;
import net.climaxmc.utilities.C;
import net.climaxmc.utilities.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdvancedKit extends Kit {
    ClimaxGames plugin;

    public AdvancedKit(ClimaxGames plugin) {
        super(C.GREEN + "Advanced", new String[]{"Advanced paintball kit.", "Jonhan will change the name at some point."}, new Ability[]{new AdvancedGunAbility(plugin)}, new I(Material.IRON_BARDING), 2000);
        this.plugin = plugin;
    }

    @Override
    public void giveItems(Player player) {
        player.getInventory().setHelmet(new I(Material.LEATHER_HELMET).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setChestplate(new I(Material.LEATHER_CHESTPLATE).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setLeggings(new I(Material.LEATHER_LEGGINGS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().setBoots(new I(Material.LEATHER_BOOTS).color(plugin.getManager().getGame().getPlayerTeam(player).getColor()));
        player.getInventory().addItem(new I(Material.IRON_BARDING).name(C.GOLD + "Advanced Gun"));
        player.getInventory().addItem(new I(Material.POTION).name(C.BLUE + "Revive Potion"));
        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 12));
    }
}
