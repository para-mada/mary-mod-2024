package com.paramada.marycum2024.mixins;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClampedEntityAttribute.class)
public class ClampedEntityAttributeMixin extends EntityAttribute {

    protected ClampedEntityAttributeMixin(String translationKey, double fallback) {
        super(translationKey, fallback);
    }

    @Inject(method = "clamp", at = @At("HEAD"), cancellable = true)
    private void rpg$unclampMaxHealth(double value, CallbackInfoReturnable<Double> cir) {
        // Solo quitar clamp para max_health; el resto sigue normal
        final String translationKey = this.getTranslationKey();
        if (translationKey.equals("attribute.name.generic.max_health")) {
            // O si prefieres no infinito: Math.min(value, 1.0E12)
            cir.setReturnValue(value);
        }
    }
}
