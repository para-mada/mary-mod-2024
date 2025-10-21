package com.paramada.marycum2024.items;

import net.minecraft.util.Formatting;

public record ItemRarity(String name, Formatting color, int tier) {
    public static final ItemRarity COMMON = new ItemRarity("common", Formatting.WHITE, 1);
    public static final ItemRarity UNCOMMON = new ItemRarity("uncommon", Formatting.YELLOW, 2);
    public static final ItemRarity RARE = new ItemRarity("rare", Formatting.AQUA, 3);
    public static final ItemRarity EPIC = new ItemRarity("epic", Formatting.LIGHT_PURPLE, 4);
    public static final ItemRarity LEGENDARY = new ItemRarity("legendary", Formatting.GOLD, 5);
    public static final ItemRarity MYTHIC = new ItemRarity("mythic", Formatting.RED, 6);
    public static final ItemRarity DIVINE = new ItemRarity("divine", Formatting.DARK_PURPLE, 7);

}
