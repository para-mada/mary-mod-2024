package com.paramada.marycum2024.mixins;

import com.mojang.authlib.GameProfile;
import com.paramada.marycum2024.events.KeyboardHandler;
import com.paramada.marycum2024.items.trinkets.bases.SpellTrinket;
import com.paramada.marycum2024.util.animator.IExampleAnimatedPlayer;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements IExampleAnimatedPlayer {

    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    private final ModifierLayer<IAnimation> modAnimationContainer = new ModifierLayer<>();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void init(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        var player = (AbstractClientPlayerEntity) (Object) this;
        var animation = PlayerAnimationAccess.getPlayerAnimLayer(player);
        animation.addAnimLayer(1000, modAnimationContainer);
    }

    @Override
    public ModifierLayer<IAnimation> maryCum2024$getModAnimation() {
        return modAnimationContainer;
    }
}
