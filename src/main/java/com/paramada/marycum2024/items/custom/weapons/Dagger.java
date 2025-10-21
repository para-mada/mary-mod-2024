package com.paramada.marycum2024.items.custom.weapons;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class Dagger extends SwordItem {
    public Dagger(ToolMaterial toolMaterial, int attackDamage, float attackSpeed) {
        super(toolMaterial, attackDamage, attackSpeed, new Settings().fireproof());
    }
}
