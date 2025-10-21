package com.paramada.marycum2024.events;

import com.paramada.marycum2024.networking.NetworkManager;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class KeyboardHandler {
    public static final String KEY_CATEGORY_MARY_MOD = "key.category.mary-mod-2024";
    public static final String KEY_USE_ITEM = "key.mary-mod-2024.use_item";
    public static final String KEY_SWITCH_ITEM = "key.mary-mod-2024.switch_item";
    public static final String KEY_SWITCH_MAIN_HAND = "key.mary-mod-2024.switch_main_hand";
    public static final String KEY_SWITCH_OFF_HAND = "key.mary-mod-2024.switch_off_hand";
    public static final String KEY_Z_TARGETING = "key.mary-mod-2024.z_targeting";
    public static final String KEY_HEAVY_ATTACK = "key.mary-mod-2024.attack_heavy";
    public static final String KEY_LIGHT_ATTACK = "key.mary-mod-2024.attack_light";
    public static final String KEY_SPELL_SELECTOR = "key.mary-mod-2024.key_spell_selector";

    public static KeyBinding useItemKey;
    public static KeyBinding switchItemNextKey;
    public static KeyBinding zTargetingKey;
    public static KeyBinding switchPrimaryHandKey;
    public static KeyBinding switchSecondaryHandKey;
    public static KeyBinding heavyAttackKey;
    public static KeyBinding lightAttackKey;

    /// SPELLS
    public static KeyBinding spellSelector;

    public static boolean SELECTOR_AVAILABLE = false;

    private static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var player = MinecraftClient.getInstance().player;
            var soulsPlayer = PlayerEntityBridge.getCurrentSoulsPlayer();
            assert soulsPlayer != null;
            if (zTargetingKey.wasPressed()) {
                final var buf = PacketByteBufs.create();
                soulsPlayer.lockFocusedTarget();
                final var target = soulsPlayer.getLockedTarget();
                if (target != null) {
                    buf.writeInt(target.getId());
                } else {
                    buf.writeInt(-1);
                }
                ClientPlayNetworking.send(NetworkManager.SYNC_LOCKED_TARGET_ID, buf);

            }

            var trinketComponent = LivingEntityBridge.getTrinketComponent(player);
            if (trinketComponent != null) {
                if (spellSelector.isPressed()) {
                    KeyboardHandler.SELECTOR_AVAILABLE = true;
                } else if (spellSelector.wasPressed() && SELECTOR_AVAILABLE) {
                    final int currentSpell = soulsPlayer.getCurrentSpellSlot();

                    final var buf = PacketByteBufs.create();
                    int nextIndex = currentSpell + 1;
                    int maxIndex = trinketComponent.getInventory().get("legs").get("spell").size();
                    if (nextIndex >= maxIndex) {
                        nextIndex -= maxIndex;
                    }

                    soulsPlayer.setCurrentSpellSlot(nextIndex);

                    buf.writeInt(nextIndex);
                    ClientPlayNetworking.send(NetworkManager.SYNC_CURRENT_SPELL_ID, buf);
                    KeyboardHandler.SELECTOR_AVAILABLE = false;
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
                }

            }
        });
    }

    public static void register() {
        useItemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_USE_ITEM,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_MARY_MOD
        ));
        switchItemNextKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SWITCH_ITEM,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_PAGE_UP,
                KEY_CATEGORY_MARY_MOD
        ));
        zTargetingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_Z_TARGETING,
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
                KEY_CATEGORY_MARY_MOD
        ));
        switchPrimaryHandKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SWITCH_MAIN_HAND,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                KEY_CATEGORY_MARY_MOD
        ));
        switchSecondaryHandKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SWITCH_OFF_HAND,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                KEY_CATEGORY_MARY_MOD
        ));
        heavyAttackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_HEAVY_ATTACK,
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                KEY_CATEGORY_MARY_MOD
        ));
        lightAttackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_LIGHT_ATTACK,
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_LEFT,
                KEY_CATEGORY_MARY_MOD
        ));


        /// Spells
        spellSelector = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SPELL_SELECTOR,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_MARY_MOD
        ));

        registerKeyInputs();
    }
}