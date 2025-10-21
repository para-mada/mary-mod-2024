package com.paramada.marycum2024.networking;

import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.items.trinkets.SpellTrinketBuilder;
import com.paramada.marycum2024.items.trinkets.bases.SpellTrinket;
import com.paramada.marycum2024.networking.packets.*;
import com.paramada.marycum2024.spells.ClientShields;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class NetworkManager {
    public static final Identifier SYNC_MONEY_ID = new Identifier(MaryMod2024.MOD_ID, "sync_money");
    public static final Identifier EARN_MONEY_ID = new Identifier(MaryMod2024.MOD_ID, "earn_money");
    public static final Identifier SPEND_MONEY_ID = new Identifier(MaryMod2024.MOD_ID, "spend_money");
    public static final Identifier REQUEST_MONEY_ID = new Identifier(MaryMod2024.MOD_ID, "request_money");
    public static final Identifier SYNC_UPGRADE_ID = new Identifier(MaryMod2024.MOD_ID, "sync_upgrade");
    public static final Identifier REQUEST_UPGRADE_ID = new Identifier(MaryMod2024.MOD_ID, "sync_upgrade");
    public static final Identifier REST_AT_EFIGY_ID = new Identifier(MaryMod2024.MOD_ID, "rest_at_efigy");
    public static final Identifier CONTINUE_GAME = new Identifier(MaryMod2024.MOD_ID, "continue_game");
    public static final Identifier INCREASE_UPGRADE_ID = new Identifier(MaryMod2024.MOD_ID, "increase_upgrade");
    public static final Identifier INCREASE_POTION_AMOUNT_ID = new Identifier(MaryMod2024.MOD_ID, "increase_potion_amount");
    public static final Identifier SYNC_DURABILITY_ID = new Identifier(MaryMod2024.MOD_ID, "sync_durability");
    public static final Identifier LEVEL_UP_ID = new Identifier(MaryMod2024.MOD_ID, "level_up");
    public static final Identifier SYNC_LEVEL_ID = new Identifier(MaryMod2024.MOD_ID, "sync_level");
    public static final Identifier REQUEST_LEVEL_ID = new Identifier(MaryMod2024.MOD_ID, "request_level");
    public static final Identifier SWAP_MAIN_HAND_ID = new Identifier(MaryMod2024.MOD_ID, "swap_main_hand_id");
    public static final Identifier START_USE_ITEM_ID = new Identifier(MaryMod2024.MOD_ID, "start_use_item_id");
    public static final Identifier CAST_SPELL_ID = new Identifier(MaryMod2024.MOD_ID, "cast_spell_id");
    public static final Identifier CREATE_MANA_SHIELD_ID = new Identifier(MaryMod2024.MOD_ID, "create_mana_shield_id");
    public static final Identifier REMOVE_MANA_SHIELD_ID = new Identifier(MaryMod2024.MOD_ID, "remove_mana_shield_id");
    public static final Identifier SYNC_LOCKED_TARGET_ID = new Identifier(MaryMod2024.MOD_ID, "sync_locked_target_id");
    public static final Identifier SYNC_CURRENT_SPELL_ID = new Identifier(MaryMod2024.MOD_ID, "sync_current_spell_id");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(EARN_MONEY_ID, EarnMoneyC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPEND_MONEY_ID, SpendMoneyC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_MONEY_ID, RequestMoneyC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_LEVEL_ID, RequestLevelC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_UPGRADE_ID, RequestUpgradeC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REST_AT_EFIGY_ID, RestAtEfigyC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CONTINUE_GAME, ContinueGameC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(INCREASE_UPGRADE_ID, IncreaseUpgradeC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(INCREASE_POTION_AMOUNT_ID, IncreasePotionAmountC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(LEVEL_UP_ID, LevelUpC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SWAP_MAIN_HAND_ID, SwapMainHandC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CAST_SPELL_ID, CastSpellC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SYNC_LOCKED_TARGET_ID, NetworkManager::syncLockedTarget);
        ServerPlayNetworking.registerGlobalReceiver(SYNC_CURRENT_SPELL_ID, NetworkManager::syncCurrentSpell);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_MONEY_ID, SyncMoneyS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_UPGRADE_ID, SyncUpgradeS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_DURABILITY_ID, SyncDurabilityUpgradeS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_LEVEL_ID, SyncLevelS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(START_USE_ITEM_ID, NotifyStartUsageS2CPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(CREATE_MANA_SHIELD_ID, (client, handler, buf, response) -> {
            UUID id = buf.readUuid();
            Vec3d c = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            float r = buf.readFloat();
            long ex = buf.readLong();
            client.execute(() -> ClientShields.ACTIVE.put(id, new ClientShields.ClientShield(id, c, r, ex)));
        });

        ClientPlayNetworking.registerGlobalReceiver(REMOVE_MANA_SHIELD_ID, (client, handler, buf, response) -> {
            UUID id = buf.readUuid();
            client.execute(() -> ClientShields.ACTIVE.remove(id));
        });
    }

    public static void swapHandWithSelectedItem(int currentItem, boolean withUse) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;

        var buf = PacketByteBufs.create();
        buf.writeVarInt(currentItem);
        buf.writeBoolean(withUse);
        ClientPlayNetworking.send(NetworkManager.SWAP_MAIN_HAND_ID, buf);
        var mainHandStack = player.getMainHandStack();
        var selectedItemStack = player.getInventory().getStack(currentItem);
        player.setStackInHand(Hand.MAIN_HAND, selectedItemStack);
        player.getInventory().setStack(currentItem, mainHandStack);
    }

    private static void syncLockedTarget(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final int target = buf.readInt();
        if (target == -1) {
            PlayerEntityBridge.getSoulsPlayer(player).setLockedTarget(null);
        }

        final var targetEntity = player.getWorld().getEntityById(target);
        PlayerEntityBridge.getSoulsPlayer(player).setLockedTarget(targetEntity);
    }

    private static void syncCurrentSpell(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        final int spellSlot = buf.readInt();
        final var soulsPlayer = PlayerEntityBridge.getSoulsPlayer(player);
        soulsPlayer.setCurrentSpellSlot(spellSlot);
    }
}