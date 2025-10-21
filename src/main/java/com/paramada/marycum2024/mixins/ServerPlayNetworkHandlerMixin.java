package com.paramada.marycum2024.mixins;

import com.paramada.marycum2024.items.custom.weapons.MagicWand;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerNetwork.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Inject(method = "lambda$initializeHandlers$5", at = @At("HEAD"))
    private static void mary$onHandSwing(ServerPlayerEntity player, Packets.C2S_AttackRequest request, WeaponAttributes attributes, WeaponAttributes.Attack attack, AttackHand hand, ServerWorld world, boolean useVanillaPacket, ServerPlayNetworkHandler handler, CallbackInfo ci) {
        // Solo lado servidor
        if (world.isClient) return;

        // Sólo cuando sostiene la varita en la mano que “swingeó”
        final ItemStack stack = player.getStackInHand(hand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND);

        if (!(stack.getItem() instanceof MagicWand wand)) return;

        // Si ya hay cooldown, no dispares (evita doble disparo si también pegó a bloque/entidad)
        if (player.getItemCooldownManager().isCoolingDown(wand)) return;

        wand.basicSpell(player, stack);
    }
}
