package com.paramada.marycum2024.items.trinkets;

import com.paramada.marycum2024.items.trinkets.bases.SpellTrinket;
import com.paramada.marycum2024.items.trinkets.handlers.SpellActionHandler;
import net.minecraft.util.Rarity;

public final class SpellTrinketBuilder {
    private int baseRange = 0;
    private int baseRadius = 1;
    private int baseCooldown = 20;
    private int baseManaCost = 0;
    private int castTime = 10;
    private Rarity rarity = Rarity.COMMON;
    private SpellActionHandler handler = (world, user, range, radius) -> {

    };

    public static SpellTrinketBuilder start() {
        return new SpellTrinketBuilder();
    }

    public SpellTrinket build() {
        return new SpellTrinket(
                rarity,
                castTime,
                baseRange,
                baseRadius,
                baseCooldown,
                baseManaCost,
                handler
        );
    }

    public SpellTrinketBuilder range(int range) {
        this.baseRange = range;
        return this;
    }

    public SpellTrinketBuilder rarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public SpellTrinketBuilder cooldown(int cooldownSeconds) {
        this.baseCooldown = cooldownSeconds * 20;
        return this;
    }

    public SpellTrinketBuilder manaCost(int manaCost) {
        this.baseManaCost = manaCost;
        return this;
    }

    public SpellTrinketBuilder radius(int radius) {
        this.baseRadius = radius;
        return this;
    }

    public SpellTrinketBuilder handler(SpellActionHandler handler) {
        this.handler = handler;
        return this;
    }


    public SpellTrinketBuilder castTime(int castTime) {
        this.castTime = castTime;
        return this;
    }
}
