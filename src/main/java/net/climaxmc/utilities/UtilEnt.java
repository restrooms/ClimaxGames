package net.climaxmc.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;

public class UtilEnt {
    private final static String obcPackage;
    private final static String nmsPackage;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        obcPackage = "org.bukkit.craftbukkit." + version + ".";
        nmsPackage = "net.minecraft.server." + version + ".";
    }

    public static void removeAI(Entity entity) {
        try {
            Object obcEntity = Class.forName(obcPackage + "entity.CraftEntity").cast(entity);
            Object nmsEntity = obcEntity.getClass().getMethod("getHandle").invoke(obcEntity);
            Object tag = Class.forName(nmsPackage + "Entity").getMethod("getNBTTag").invoke(nmsEntity);
            if (tag == null) {
                tag = Class.forName(nmsPackage + "NBTTagCompound").newInstance();
            }
            nmsEntity.getClass().getMethod("c", Class.forName(nmsPackage + "NBTTagCompound")).invoke(nmsEntity, tag);
            tag.getClass().getMethod("setInt", String.class, int.class).invoke(tag, "NoAI", 1);
            tag.getClass().getMethod("setBoolean", String.class, boolean.class).invoke(tag, "Silent", true);
            nmsEntity.getClass().getMethod("f", Class.forName(nmsPackage + "NBTTagCompound")).invoke(nmsEntity, tag);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
