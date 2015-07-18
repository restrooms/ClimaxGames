package net.climaxmc.managers;

import com.google.common.base.Optional;
import net.climaxmc.kit.Kit;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.DisplayNameData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class GameLobbyManager extends Manager {
    GameLobbyManager() {
        initializeKitSelector();
    }

    private void initializeKitSelector() {
        Optional<World> worldOptional = plugin.getGame().getServer().getWorld("world");

        if (worldOptional.isPresent()) {
            World world = worldOptional.get();

            plugin.getLogger().info("Parsing world for kit selector entity locations.");

            int blocksProcessed = 0;
            int amountOfKitEntities = 0;

            for (int x = -200; x < 200; x++) {
                for (int z = -200; z < 200; z++) {
                    for (int y = 0; y < 254; y++) {
                        blocksProcessed++;

                        if (blocksProcessed % 2000000 == 0) {
                            plugin.getLogger().info("Processed: " + blocksProcessed);
                        }

                        Location location = world.getLocation(x, y + 1, z);

                        BlockState block = world.getBlock(location.add(0, -1, 0).getBlockPosition());

                        if (!block.getType().equals(BlockTypes.DOUBLE_STONE_SLAB)) {
                            continue;
                        }

                        if (world.getBlock(location.getBlockPosition()).getType().equals(BlockTypes.AIR)) {
                            if (amountOfKitEntities == manager.getGame().getKits().length) {
                                continue;
                            }

                            Kit kit = manager.getGame().getKits()[amountOfKitEntities];

                            Optional<Entity> entityOptional = world.createEntity(EntityTypes.ZOMBIE, location.getBlockPosition());

                            if (entityOptional.isPresent()) {
                                Zombie zombie = (Zombie) entityOptional.get();
                                kit.apply(zombie);

                                Optional<DisplayNameData> nameDataOptional = zombie.getData(DisplayNameData.class);
                                if (nameDataOptional.isPresent()) {
                                    DisplayNameData nameData = nameDataOptional.get();
                                    nameData.setDisplayName(Texts.of(kit.getName()));
                                    nameData.setCustomNameVisible(true);
                                }

                                if (world.spawnEntity(zombie)) {
                                    plugin.getLogger().info("Spawned entity at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ".");
                                }
                            }

                            amountOfKitEntities++;
                        }
                    }
                }
            }

            plugin.getLogger().info("World parsing completed!");
        }
    }
}
