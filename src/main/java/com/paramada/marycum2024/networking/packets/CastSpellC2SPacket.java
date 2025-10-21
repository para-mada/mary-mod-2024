package com.paramada.marycum2024.networking.packets;

import com.paramada.marycum2024.items.trinkets.bases.SpellTrinket;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;

public class CastSpellC2SPacket {
    public static <T extends FabricPacket> void receive(MinecraftServer server, ServerPlayerEntity player,
                                                        ServerPlayNetworkHandler handler,
                                                        PacketByteBuf buf, PacketSender responseSender) {

        final var trinketComponent = LivingEntityBridge.getTrinketComponent(player);
        final int spellSlot = buf.readInt();
        final var spell = trinketComponent.getEquipped(stack -> stack.getItem() instanceof SpellTrinket).stream().filter(spellData -> spellData.getLeft().index() == spellSlot).findFirst();

        if (spell.isPresent()) {
            final var spellStack = spell.get().getRight().getItem();
            if (spellStack instanceof SpellTrinket spellItem) {
                spellItem.castSpell(player.getWorld(), player, Hand.MAIN_HAND);
            }
        }
    }
}
