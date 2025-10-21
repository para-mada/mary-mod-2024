package com.paramada.marycum2024.items.trinkets.bases;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.UUID;

public class AccessoryTrinket extends TrinketItem {

    private final List<StatusEffectInstance> effects;
    private final Multimap<EntityAttribute, EntityAttributeModifier> modifiers;
    private int additionalSlots = 0;

    public AccessoryTrinket(Rarity rarity, List<StatusEffectInstance> effects, Multimap<EntityAttribute, EntityAttributeModifier> modifiers) {
        super(new Settings().maxCount(1).rarity(rarity).fireproof());
        this.effects = effects;
        this.modifiers = modifiers;
    }

    public AccessoryTrinket(Rarity rarity, List<StatusEffectInstance> effects) {
        this(rarity, effects, ImmutableMultimap.of());
    }

    public AccessoryTrinket(Rarity rarity, Multimap<EntityAttribute, EntityAttributeModifier> modifiers) {
        this(rarity, List.of(), modifiers);
    }

    public AccessoryTrinket(Rarity rarity, Multimap<EntityAttribute, EntityAttributeModifier> modifiers, int additionalSlots) {
        super(new Settings().maxCount(1).rarity(rarity).fireproof());
        this.additionalSlots = additionalSlots;
        final var mutableModifiers = HashMultimap.create(modifiers);
        if (this.additionalSlots > 0) {
            SlotAttributes.addSlotModifier(mutableModifiers, "legs/spell", UUID.randomUUID(), additionalSlots, EntityAttributeModifier.Operation.ADDITION);
        }
        this.effects = List.of();
        this.modifiers = mutableModifiers;
    }

    public AccessoryTrinket() {
        this(Rarity.COMMON, List.of(), ImmutableMultimap.of());
    }

    public List<StatusEffectInstance> getEffects() {
        return effects;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        modifiers.putAll(this.modifiers);
        return modifiers;
    }
}
