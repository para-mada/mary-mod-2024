package com.paramada.marycum2024.entities.projectiles;

import com.paramada.marycum2024.attributes.ModEntityAttributes;
import com.paramada.marycum2024.entities.ModEntities;
import com.paramada.marycum2024.items.custom.weapons.MagicWand;
import com.paramada.marycum2024.souls.SoulsPlayer;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SpellProjectileEntity extends ThrownItemEntity {

    private static final float BASE_DAMAGE = 1f;

    public SpellProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SpellProjectileEntity(LivingEntity owner, World world) {
        super(ModEntities.SPELL_PROJECTILE, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    @Override
    protected boolean canHit(Entity entity) {
        final var owner = this.getOwner();
        if (owner instanceof PlayerEntity player) {
            final SoulsPlayer soulsPlayer = PlayerEntityBridge.getSoulsPlayer(player);
            if (soulsPlayer.hasLockedTarget()) {
                final var targetUUID = soulsPlayer.getLockedTarget().getUuid();
                final var collissionUUID = entity.getUuid();
                return collissionUUID.equals(targetUUID);
            }
        }
        return super.canHit(entity);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        final var owner = this.getOwner();
        final Entity target = entityHitResult.getEntity();
        assert target != null;
        if (this.canHit(target)) {
            if (owner != null) {
                float finalDamage = BASE_DAMAGE;
                if (owner instanceof LivingEntity livingEntity) {
                    final ItemStack stack = livingEntity.getStackInHand(Hand.MAIN_HAND);
                    final Item wand = stack.getItem();
                    final var attributes = wand.getAttributeModifiers(stack, EquipmentSlot.MAINHAND).get(ModEntityAttributes.MAGIC_DAMAGE);
                    final double sums = attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.ADDITION).mapToDouble(EntityAttributeModifier::getValue).sum();
                    final double bases = Math.max(1, attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE).mapToDouble(EntityAttributeModifier::getValue).sum());
                    final double totals = Math.max(1, attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL).mapToDouble(EntityAttributeModifier::getValue).sum());

                    finalDamage = (float) (((BASE_DAMAGE * bases) + sums) * totals) * 10;
                }
                target.damage(owner.getDamageSources().create(DamageTypes.MAGIC, owner), finalDamage);
            } else {
                target.damage(this.getDamageSources().indirectMagic(this, null), BASE_DAMAGE);
            }
            discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        discard();
    }
}
