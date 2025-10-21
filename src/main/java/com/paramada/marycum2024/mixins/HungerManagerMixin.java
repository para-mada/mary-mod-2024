package com.paramada.marycum2024.mixins;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mixin a HungerManager
@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {

    // Evita que se acumule agotamiento (correr, minar, etc.)
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void mary$noExhaustion(float amount, CallbackInfo ci) {
        ci.cancel();
    }

    // Evita que el update baje comida / aplique daño por inanición
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void mary$noHunger(PlayerEntity player, CallbackInfo ci) {
        ci.cancel();
    }
}

