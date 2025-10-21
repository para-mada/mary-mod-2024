package com.paramada.marycum2024.attributes.mana;

import com.paramada.marycum2024.attributes.ModComponents;
import com.paramada.marycum2024.attributes.ModEntityAttributes;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class ManaComponentImpl implements ManaComponent, AutoSyncedComponent {
    private int current = 10;
    private int max = 10;
    private static final int BASE_REGEN_PER_SECOND = 4;
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
        int additionalMax = (int) player.getAttributeValue(ModEntityAttributes.ADDITIONAL_MANA);
        return max + additionalMax * 10;
    }
    @Override
    public int getRegenPerSecond() {
        return BASE_REGEN_PER_SECOND;
    }
    @Override
    public int getRegenPerSecond(PlayerEntity player) {
        int additionalRegen = (int) player.getAttributes().getValue(ModEntityAttributes.MANA_REGENERATION);
        return BASE_REGEN_PER_SECOND + additionalRegen * 10;
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
        int manaPerSecond = getRegenPerSecond(player);
        int max = getMax(player);
        final int manaToRegen = manaPerSecond / 4;
        if (current > max){
            current = max;
            ModComponents.MANA.sync(player);
        }
        if (tickAccumulator >= 5) {
            tickAccumulator -= 5;


            if (current < max) {
                int toRegen = current + manaToRegen;
                current = Math.min(max, toRegen);
                ModComponents.MANA.sync(player);
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
        ModComponents.MANA.sync(player);
    }
}
