package com.paramada.marycum2024.util.functionality.bridges;

import com.paramada.marycum2024.util.functionality.IEntityDataSaver;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;

public class LivingEntityBridge {
    public static NbtCompound getPersistentData(Entity entity) {
        var living = (IEntityDataSaver) entity;
        return living.maryCum2024$getPersistentData();
    }

    public static Inventory getInventory(Entity entity) {
        if (entity instanceof PlayerEntity living) {
            return living.getInventory();
        }
        return null;
    }

    public static Inventory getEnderInventory(Entity entity) {
        if (entity instanceof PlayerEntity living) {
            return living.getEnderChestInventory();
        }
        return null;
    }
    public static boolean hasFlag(final Entity entity, final String flag) {
        return getPersistentData(entity).getBoolean(flag);
    }

    public static boolean isCreative(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            return player.isCreative();
        }
        return false;
    }

    public static TrinketComponent getTrinketComponent(LivingEntity player) {
        if (player == null) {
            return null;
        }
        var trinketComponent = TrinketsApi.getTrinketComponent(player);
        return trinketComponent.orElse(null);
    }
}