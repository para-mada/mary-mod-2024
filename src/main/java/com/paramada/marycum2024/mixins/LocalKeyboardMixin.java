package com.paramada.marycum2024.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
@Environment(EnvType.CLIENT)
public class LocalKeyboardMixin {


    @Inject(at = @At("HEAD"), method = "onKey", cancellable = true)
    private void avoidOnKeyF3(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        var client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        var player = client.player;
        if (player == null) {
            return;
        }
        if (player.hasPermissionLevel(3)) {
            return;
        }

        if (key == GLFW.GLFW_KEY_F3) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "processF3", cancellable = true)
    private void avoidProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        var client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        var player = client.player;
        if (player == null) {
            return;
        }
        if (player.hasPermissionLevel(3)) {
            return;
        }

        cir.setReturnValue(false);
    }
}
