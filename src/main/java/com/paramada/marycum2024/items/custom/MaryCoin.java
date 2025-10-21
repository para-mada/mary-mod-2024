package com.paramada.marycum2024.items.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaryCoin extends MaryItem {
    public MaryCoin(Rarity rarity) {
        super(new Settings().fireproof().rarity(rarity).maxCount(100));
    }
}
