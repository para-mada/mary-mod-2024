package com.paramada.marycum2024.mixins;

import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import com.paramada.marycum2024.util.souls.ISoulsPlayerCamera;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final
    MinecraftClient client;

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
    private void overrideCrosshairTarget(float tickDelta, CallbackInfo ci) {
        if(this.client.player != null) {
            var soulsPlayer = PlayerEntityBridge.getCurrentSoulsPlayer();
            if(soulsPlayer != null) {
                if (soulsPlayer.hasLockedTarget()) {
                    client.crosshairTarget = new EntityHitResult(soulsPlayer.getLockedTarget());
                    client.targetedEntity = soulsPlayer.getLockedTarget();
                    ci.cancel();
                }
            }
        }
    }
}
