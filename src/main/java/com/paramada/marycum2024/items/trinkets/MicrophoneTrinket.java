package com.paramada.marycum2024.items.trinkets;

import com.google.common.collect.Multimap;
import com.paramada.marycum2024.attributes.ModEntityAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

import java.util.UUID;

public class MicrophoneTrinket extends TrinketItem {
    public MicrophoneTrinket() {
        super((new Item.Settings().rarity(Rarity.RARE).maxCount(1).fireproof()));
    }


    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        modifiers.put(ModEntityAttributes.DISTANCE_DAMAGE, new EntityAttributeModifier("mic_dmg_mlt", 1.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        return modifiers;
    }
}
