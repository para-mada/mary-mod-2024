package com.paramada.marycum2024.mixins;

import com.paramada.marycum2024.effects.ModEffects;
import com.paramada.marycum2024.events.CustomExplosion;
import com.paramada.marycum2024.items.ItemManager;
import com.paramada.marycum2024.items.custom.weapons.GuardianShield;
import com.paramada.marycum2024.items.custom.weapons.MagicWand;
import com.paramada.marycum2024.items.trinkets.bases.SpellTrinket;
import com.paramada.marycum2024.souls.SoulsPlayer;
import com.paramada.marycum2024.util.functionality.PerformanceCooldownManager;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import com.paramada.marycum2024.util.souls.ISoulsPlayer;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ISoulsPlayer {

    @Unique
    private SoulsPlayer soulsPlayer;

    @Unique
    private final PerformanceCooldownManager<CustomExplosion> explosionContainer = new PerformanceCooldownManager<>();

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Shadow
    public abstract float getAttackCooldownProgress(float baseTime);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        soulsPlayer = new SoulsPlayer(this);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    protected void onTick(CallbackInfo ci) {
        if (!this.explosionContainer.isAlive()) {
            if (this.getAttackCooldownProgress(0.5f) > 0.9f) {
                explosionContainer.revive();
            }
        }
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"), method = "travel")
    protected Vec3d onTravel(Vec3d movementInput) {
        if (this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GuardianShield shield && shield.isChanneling()) {
            return movementInput.multiply(0, 1, 0);
        }

        return movementInput;
    }

    @Inject(at = @At("HEAD"), method = "jump", cancellable = true)
    protected void onJump(CallbackInfo ci) {
        if (this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GuardianShield shield && shield.isChanneling()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    protected void onEndTick(CallbackInfo ci) {
        if (soulsPlayer != null) {
            soulsPlayer.tick();
            if (!this.isUsingItem()) {
                var animator = PlayerEntityBridge.getAnimator();
                if (animator != null && !animator.isActive()) {
                    soulsPlayer.stopUsingItem();
                    animator.setAnimation(null);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "canFoodHeal", cancellable = true)
    private void onFoodHeal(CallbackInfoReturnable<Boolean> cir) {
        if (this.hasStatusEffect(ModEffects.ZOMBIEFICATION) || this.hasStatusEffect(ModEffects.VAMPIRISM)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER), method = "attack")
    private void whenDamageEntity(Entity target, CallbackInfo ci) {
        var player = (PlayerEntity) (LivingEntity) this;
        var trinketComponent = LivingEntityBridge.getTrinketComponent(player);
        if (trinketComponent.isEquipped(ItemManager.MICROPHONE_TRINKET)) {
            var world = this.getWorld();
            explosionContainer.put(new CustomExplosion(world, player, target, 5));
        }
    }

    @Inject(at = @At("RETURN"), method = "attack")
    private void atEndOfAttack(Entity target, CallbackInfo ci) {
        if (explosionContainer.hasValue()) {
            explosionContainer.perform();
        }
    }

    @Override
    public SoulsPlayer marymod2024$getSoulsPlayer() {
        if (soulsPlayer == null) {
            soulsPlayer = new SoulsPlayer(this);
        }
        return soulsPlayer;
    }
}
