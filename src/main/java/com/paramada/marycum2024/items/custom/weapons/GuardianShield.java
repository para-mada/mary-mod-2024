package com.paramada.marycum2024.items.custom.weapons;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.paramada.marycum2024.items.ItemRarity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

public class GuardianShield extends AxeItem {

    private final Multimap<EntityAttribute, EntityAttributeModifier> handModifiers;
    private final Multimap<EntityAttribute, EntityAttributeModifier> offHandModifiers;
    private final Multimap<EntityAttribute, EntityAttributeModifier> channelingModifiers;
    private static final Multimap<EntityAttribute, EntityAttributeModifier> USAGE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_speed", -1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
    );
    private static final List<StatusEffectInstance> USAGE_EFFECTS = List.of(
            new StatusEffectInstance(StatusEffects.ABSORPTION, StatusEffectInstance.INFINITE, 1, false, false)
    );
    private final ItemRarity itemRarity;
    private boolean isChanneling = false;
    private boolean wasSelected = false;

    public GuardianShield(
            float attackDamage,
            float attackSpeed,
            ItemRarity rarity,
            Multimap<EntityAttribute, EntityAttributeModifier> handModifiers,
            Multimap<EntityAttribute, EntityAttributeModifier> offHandModifiers,
            Multimap<EntityAttribute, EntityAttributeModifier> channelingModifiers
    ) {
        super(ToolMaterials.IRON, attackDamage, attackSpeed, new Settings().fireproof());
        this.handModifiers = handModifiers;
        this.offHandModifiers = offHandModifiers;
        this.channelingModifiers = channelingModifiers;
        this.itemRarity = rarity;
    }

    @Override
    public Text getName() {
        return super.getName().copy().formatted(this.itemRarity.color());
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().formatted(this.itemRarity.color());
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.mary-mod-2024.on_use").append(Text.literal(":")).formatted(Formatting.GRAY));
        final DecimalFormat df = new DecimalFormat("+#.#;-#.#");
        this.channelingModifiers.forEach((attribute, modifier) -> {
            final String value = df.format(modifier.getValue());
            final String multiplyValue = df.format(modifier.getValue() * 100);
            switch (modifier.getOperation()) {
                case ADDITION, MULTIPLY_BASE -> {
                    tooltip.add(Text.translatable(attribute.getTranslationKey()).append(Text.literal(": ")).append(Text.literal(value)).formatted(Formatting.BLUE));
                }
                case MULTIPLY_TOTAL -> {
                    tooltip.add(Text.translatable(attribute.getTranslationKey()).append(Text.literal(": ")).append(Text.literal(multiplyValue)).append(Text.literal("%")).formatted(Formatting.BLUE));
                }
            }
        });
        GuardianShield.USAGE_MODIFIERS.forEach((attribute, modifier) -> {
            final String value = df.format(modifier.getValue());
            final String multiplyValue = df.format(modifier.getValue() * 100);
            switch (modifier.getOperation()) {
                case ADDITION, MULTIPLY_BASE -> {
                    tooltip.add(Text.translatable(attribute.getTranslationKey()).append(Text.literal(": ")).append(Text.literal(value)).formatted(Formatting.BLUE));
                }
                case MULTIPLY_TOTAL -> {
                    tooltip.add(Text.translatable(attribute.getTranslationKey()).append(Text.literal(": ")).append(Text.literal(multiplyValue)).append(Text.literal("%")).formatted(Formatting.BLUE));
                }
            }
        });
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final ItemStack stack = user.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND && !user.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            this.isChanneling = true;
            user.getItemCooldownManager().set(this, 20 * 60);
            user.getAttributes().addTemporaryModifiers(USAGE_MODIFIERS);
            user.getAttributes().addTemporaryModifiers(channelingModifiers);
            world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1f, 1f);
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.5f, 1f);
        }
        user.setCurrentHand(hand);
        return super.use(world, user, hand);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20 * 50;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        this.isChanneling = false;
        user.getAttributes().removeModifiers(USAGE_MODIFIERS);
        user.getAttributes().removeModifiers(channelingModifiers);
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.isChanneling = false;
        user.getAttributes().removeModifiers(USAGE_MODIFIERS);
        user.getAttributes().removeModifiers(channelingModifiers);
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected) {
            wasSelected = true;
        }
        if (!selected && wasSelected && entity instanceof LivingEntity user) {
            wasSelected = false;
            user.getAttributes().removeModifiers(channelingModifiers);
            user.getAttributes().removeModifiers(USAGE_MODIFIERS);
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        final var modifiers = HashMultimap.create(super.getAttributeModifiers(slot));
        switch (slot) {
            case OFFHAND -> modifiers.putAll(offHandModifiers);
            case MAINHAND -> {
                modifiers.putAll(handModifiers);
            }
        }
        return modifiers;
    }

    public static boolean isChanneling(ItemStack stack) {
        return stack.getItem() instanceof GuardianShield gs && gs.isChanneling;
    }

    public boolean isChanneling() {
        return this.isChanneling;
    }
}
