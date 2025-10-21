package com.paramada.marycum2024.items.custom.weapons;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.paramada.marycum2024.attributes.ModComponents;
import com.paramada.marycum2024.client.SpellPreviewState;
import com.paramada.marycum2024.entities.projectiles.SpellProjectileEntity;
import com.paramada.marycum2024.items.ItemRarity;
import com.paramada.marycum2024.items.trinkets.bases.SpellTrinket;
import com.paramada.marycum2024.util.functionality.SpellCastHelper;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MagicWand extends AxeItem {

    private final Multimap<EntityAttribute, EntityAttributeModifier> modifiers;
    private final ItemRarity itemRarity;

    public MagicWand(float attackSpeed, Multimap<EntityAttribute, EntityAttributeModifier> modifiers, ItemRarity rarity) {
        super(ToolMaterials.NETHERITE, -5f, attackSpeed, new Settings().fireproof());
        this.modifiers = modifiers;
        this.itemRarity = rarity;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Start Casting
        user.setCurrentHand(hand);
        return super.use(world, user, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) {
            PlayerEntity player = (PlayerEntity) user;
            var soulsPlayer = PlayerEntityBridge.getSoulsPlayer(player);

            final SpellTrinket spell = soulsPlayer.getCurrentSpell();
            SpellPreviewState.center = SpellCastHelper.spellRayCast(user.getWorld(), player, spell != null ? spell.range(player) : 0f, 0f, false);
            SpellPreviewState.radius = spell != null ? spell.radius(player) : 0f;
            SpellPreviewState.active = true;
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        final var soulsPlayer = PlayerEntityBridge.getSoulsPlayer((PlayerEntity) user);
        final var spell = soulsPlayer.getCurrentSpell();
        if (spell == null) {
            return;
        }
        int i = this.getMaxUseTime(stack, spell, user) - remainingUseTicks;
        float f = getPullProgress(i);

        spell.castSpell(world, (PlayerEntity) user, Hand.MAIN_HAND);

        SpellPreviewState.active = false;
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public Text getName() {
        return super.getName().copy().formatted(this.itemRarity.color());
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().formatted(this.itemRarity.color());
    }

    public void basicSpell(PlayerEntity user, ItemStack stack) {
        final World world = user.getWorld();
        final var mana = ModComponents.MANA.get(user);
        if (!mana.tryConsume(10)) {
            return;
        }
        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE,
                SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!world.isClient) {
            SpellProjectileEntity projectileEntity = new SpellProjectileEntity(user, world);
            projectileEntity.setItem(Items.FIRE_CHARGE.getDefaultStack());
            projectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0F);
            world.spawnEntity(projectileEntity);
        }


        user.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public static float getPullProgress(int useTicks) {
        return (float)useTicks / 20.0F;
    }

    public int getMaxUseTime(ItemStack stack, SpellTrinket spell, LivingEntity user) {
        return 72000;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> immutable = super.getAttributeModifiers(slot);
        var modifiers = HashMultimap.create(immutable);
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.putAll(this.modifiers);
        }
        return modifiers;
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        return false;
    }
}
