package com.paramada.marycum2024.attributes.shield;

import com.paramada.marycum2024.attributes.ModComponents;
import com.paramada.marycum2024.attributes.ModEntityAttributes;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ShieldComponentImpl implements ShieldComponent, AutoSyncedComponent {
    private int current = 0;
    private int max = 10;
    private int tickAccumulator = 0; // para 20 tps → +regen cada segundo

    @Override
    public int getCurrent() {
        return current;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public int getMax(PlayerEntity player) {
        int additionalMax = 0; //(int) player.getAttributeValue(ModEntityAttributes.ADDITIONAL_SHIELD);
        return max + additionalMax * 10;
    }

    @Override
    public void setCurrent(int v) {
        current = Math.max(0, Math.min(v, max));
    }

    @Override
    public void setMax(int v) {
        max = Math.max(1, v);
        if (current > max) current = max;
    }

    @Override
    public boolean tryConsume(int amount) {
        if (amount <= 0) return true;
        if (current >= amount) {
            current -= amount;
            return true;
        }
        return false;
    }

    @Override
    public void restore(int amount) {
        if (amount <= 0) return;
        current = Math.min(max, current + amount);
    }

    @Override
    public void tick(ServerPlayerEntity player) {
        tickAccumulator++;
        int max = getMax(player);
        if (current > max) {
            current = max;
            ModComponents.SHIELD.sync(player);
        }
        if (tickAccumulator >= 5) {
            tickAccumulator -= 5;

            if (current < max) {
                ModComponents.SHIELD.sync(player);
            }
        }
    }

    // ===== Persistencia NBT =====
    @Override
    public void readFromNbt(NbtCompound tag) {
        current = tag.getInt("current");
        max = tag.getInt("max");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("current", current);
        tag.putInt("max", max);
    }

    @Override
    public void sync(ServerPlayerEntity player) {
        ModComponents.SHIELD.sync(player);
    }
}
