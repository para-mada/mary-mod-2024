package com.paramada.marycum2024.items.trinkets.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface SpellActionHandler {
    void handle(World world, PlayerEntity user, int range, int radius);
}
