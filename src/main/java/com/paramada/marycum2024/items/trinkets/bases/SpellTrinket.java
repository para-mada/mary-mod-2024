package com.paramada.marycum2024.items.trinkets.bases;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.paramada.marycum2024.attributes.ModComponents;
import com.paramada.marycum2024.attributes.mana.ManaComponent;
import com.paramada.marycum2024.items.trinkets.handlers.SpellActionHandler;
import com.paramada.marycum2024.util.functionality.MadaMathHelper;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class SpellTrinket extends TrinketItem {

    private final SpellActionHandler handler;
    private final int baseRange;
    private final int baseRadius;
    private final int baseCooldown;
    private final int baseManaCost;
    private final int baseCastTime;


    public SpellTrinket(Rarity rarity, int baseCastTime, int baseRange, int baseRadius, int baseCooldownSeconds, int baseManaCost, SpellActionHandler handler) {
        super(new Settings().maxCount(1).rarity(rarity).fireproof());
        this.handler = handler;
        this.baseRange = baseRange;
        this.baseRadius = baseRadius;
        this.baseCooldown = baseCooldownSeconds;
        this.baseManaCost = baseManaCost;
        this.baseCastTime = baseCastTime;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        final Text text = Text.translatable("tooltip.attribute.mana_cost")
                .append(": ")
                .append(
                        Text.literal("%s".formatted(MadaMathHelper.humanizeDouble(this.manaCost(player)))).formatted(Formatting.BLUE)
                )
                .formatted(Formatting.DARK_AQUA);
        tooltip.add(text);
    }

    public int range(PlayerEntity user) {
        return baseRange;
    }

    public int radius(PlayerEntity player) {
        return baseRadius;
    }

    public int cooldown(PlayerEntity player) {
        return baseCooldown;
    }

    public int manaCost(PlayerEntity player) {
        return baseManaCost;
    }

    public int castTime(PlayerEntity user) {
        return baseCastTime;
    }

    public void castSpell(World world, PlayerEntity caster, Hand hand) {
        if (world.isClient) {
            return;
        }

        ManaComponent manaComponent = ModComponents.MANA.get(caster);

        var cooldownManager = caster.getItemCooldownManager();
        if (cooldownManager.isCoolingDown(this)) {
            caster.sendMessage(Text.literal("%s en enfriamiento".formatted(this.getName().getString())), true);
            return;
        }

        final int cost = manaCost(caster);
        final boolean ok = manaComponent.tryConsume(cost);

        if (!ok) {
            caster.sendMessage(Text.literal("No tienes suficiente maná"), true);
            caster.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 1);
            return;
        }

        handler.handle(world, caster, range(caster), radius(caster));
        caster.getItemCooldownManager().set(this, cooldown(caster));
    }
}

/*
 * cone attack
 * mana absorption
 * fireball (throw projectile) DONE
 * heal (select zone and heal) DONE
 * electro ray (constant damage to target)
 * morale (your surroundings) DONE
 * fire wall
 * protective wall
 * water freeze on wall
 * magic shield
 * magic boosts
 * selective heal
 * taunt
 * selective debuff
 * remote loot
 * thorns spell
 * mana transfer
 * terrain liquify
 * lure spell
 * ender pearl
 * Revive
 * Smite (instant damage)
 * Summon Entity
 * Summon Attack
 * Summon Trap
 * Detection
 * Arrow rain
 * Piercing Arrow
 * Locked/Homing Arrow
 * Stealth
 * Hunters Mark
 * Rope Arrow
 * Pylon Swap
 * Crit. Chance
 * Increase Range
 * special shape areas
 * cast in pylon
 *
 * assault (rush)
 * intimidate
 *
 *
 */