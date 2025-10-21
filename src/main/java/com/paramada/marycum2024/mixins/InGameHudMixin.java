package com.paramada.marycum2024.mixins;

import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.attributes.ModComponents;
import com.paramada.marycum2024.attributes.mana.ManaComponent;
import com.paramada.marycum2024.hud.HudElement;
import com.paramada.marycum2024.souls.SoulsPlayer;
import com.paramada.marycum2024.util.functionality.MadaMathHelper;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow
    private int ticks;
    @Shadow
    private int lastHealthValue;
    @Shadow
    private long lastHealthCheckTime;
    @Shadow
    private long heartJumpEndTick;
    @Shadow
    private int renderHealthValue;
    @Final
    @Shadow
    private Random random;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    protected abstract void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed);

    @Unique
    private static final Identifier MARYCOIN_TEXTURE = new Identifier(MaryMod2024.MOD_ID, "textures/hud/coin.png");
    @Unique
    private static final Identifier BAR_BACKGROUND = new Identifier(MaryMod2024.MOD_ID, "textures/hud/bar_outline.png");
    @Unique
    private static final Identifier BAR_FILL = new Identifier(MaryMod2024.MOD_ID, "textures/hud/bar_fill.png");
    @Unique
    private static final Identifier HEART_ICON = new Identifier(MaryMod2024.MOD_ID, "textures/hud/icon_heart.png");
    @Unique
    private static final Identifier MANA_ICON = new Identifier(MaryMod2024.MOD_ID, "textures/hud/icon_mana.png");
    @Unique
    private static final Identifier HUD_FONT = new Identifier(MaryMod2024.MOD_ID, "volya");
    @Unique
    private static final Identifier EXP_BACK_GROUND = new Identifier(MaryMod2024.MOD_ID, "textures/hud/bar_outline_exp.png");
    @Unique
    private static final Identifier EXP_FILL = new Identifier(MaryMod2024.MOD_ID, "textures/hud/bar_fill_exp.png");
    @Unique
    private static final Identifier SPELL_SLOT = new Identifier(MaryMod2024.MOD_ID, "textures/hud/spell_slot_hud.png");
    @Unique
    private HudElement ECONOMY;

    @Unique
    private void init() {
        int windowWidth = this.scaledWidth;
        int windowHeight = this.scaledHeight;
        ECONOMY = new HudElement(
                windowWidth - 80,
                windowHeight - 24 - 2,
                MARYCOIN_TEXTURE,
                56,
                56,
                56,
                56
        );

    }

    @Unique
    private void renderEconomy(DrawContext context) {
        TextRenderer textRenderer = getTextRenderer();
        assert client.player != null;
        int balance = LivingEntityBridge.getPersistentData(client.player).getInt("coins");
        String balanceString = new DecimalFormat("000").format(balance);

        int fontHeight = textRenderer.fontHeight;
        int textWidth = textRenderer.getWidth(balanceString);
        int textureSize = 16;
        int boxPadding = 2;
        ECONOMY.render(context, textureSize, textureSize);

        context.fill(ECONOMY.x - boxPadding, ECONOMY.y - boxPadding, ECONOMY.x + textWidth + textureSize + boxPadding * 2, ECONOMY.y + fontHeight + (textureSize / 2) + boxPadding, 0xAA000000);
        context.drawText(getTextRenderer(), Text.literal(balanceString), ECONOMY.x + textureSize + boxPadding, ECONOMY.y + (textureSize / 2) - (getTextRenderer().fontHeight / 2), 0xFFFFFFFF, false);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        init();
        renderEconomy(context);
    }

    @Unique
    private void renderCustomHealth(DrawContext context, int x, int y, float maxHealth, float health, int shield) {

        final String humanizedHealth = MadaMathHelper.humanizeDouble(health);
        final String humanizedMaxHealth = MadaMathHelper.humanizeDouble(maxHealth);

        final String renderedText = "%s/%s".formatted(humanizedHealth, humanizedMaxHealth);
        TextRenderer textRenderer = getTextRenderer();

        int fontHeight = textRenderer.fontHeight;
        int textWidth = textRenderer.getWidth(renderedText);

        int spriteWidth = 86;
        int percentagePixels = (int) ((health * (spriteWidth - 2)) / maxHealth);
        int spriteHeight = 9;
        int textureWidth = 86;
        int textureHeight = 7;

        context.drawTexture(BAR_BACKGROUND, x, y, spriteWidth, spriteHeight, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        context.setShaderColor(203 / 255f, 48 / 255f, 64 / 255f, 1);
        context.drawTexture(BAR_FILL, x + 1, y + 1, percentagePixels, spriteHeight - 2, 0, 0, percentagePixels, textureHeight - 2, textureWidth - 2, textureHeight - 2);
        context.drawTexture(HEART_ICON, x - 10, y, 9, 9, 0, 0, 9, 9, 9, 9);
        context.setShaderColor(1, 1, 1, 1);
        context.drawText(
            getTextRenderer(),
            Text.literal(renderedText).setStyle(Style.EMPTY.withFont(HUD_FONT)),
            x + (spriteWidth / 2) - (textWidth / 2),
            y + (spriteHeight / 2) - (fontHeight / 2) + 1,
            0xFFFFFFFF,
            false
        );
    }

    @Unique
    private void renderSelectedSpell(DrawContext context, int x, int y, final int spriteSize) {
        final SoulsPlayer soulsPlayer = Objects.requireNonNull(PlayerEntityBridge.getCurrentSoulsPlayer());
        final var spell = soulsPlayer.getCurrentSpell();
        context.drawTexture(SPELL_SLOT, x, y, 0, 0, spriteSize, spriteSize, spriteSize, spriteSize);

        if (spell != null) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;

            final ManaComponent mana = ModComponents.MANA.get(player);
            if (mana.getCurrent() >= spell.manaCost(player)) {
                renderItem(context, spell.getDefaultStack(), x + 2, y + 2);
            } else {
                renderExpensiveItem(context, spell.getDefaultStack(), x + 2, y + 2);
            }
        }
    }

    @Unique
    private void renderCustomMana(DrawContext context, int x, int y, float maxMana, int mana) {
        final String renderedText = "%s/%s".formatted(MadaMathHelper.humanizeDouble(mana), MadaMathHelper.humanizeDouble(maxMana));
        TextRenderer textRenderer = getTextRenderer();

        int fontHeight = textRenderer.fontHeight;
        int textWidth = textRenderer.getWidth(renderedText);

        int spriteWidth = 86;
        int percentagePixels = (int) ((mana * (spriteWidth - 2)) / maxMana);
        int spriteHeight = 9;
        int textureWidth = 86;
        int textureHeight = 7;

        context.drawTexture(BAR_BACKGROUND, x, y, spriteWidth, spriteHeight, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        context.setShaderColor(33 / 255f, 143 / 255f, 246 / 255f, 1);
        context.drawTexture(BAR_FILL, x + 1, y + 1, percentagePixels, spriteHeight - 2, 0, 0, percentagePixels, textureHeight - 2, textureWidth - 2, textureHeight - 2);
        context.setShaderColor(45 / 255f, 124 / 255f, 255 / 255f, 1);
        context.drawTexture(MANA_ICON, x + spriteWidth + 1, y, 9, 9, 0, 0, 9, 9, 9, 9);
        context.setShaderColor(1, 1, 1, 1);
        context.drawText(
                getTextRenderer(),
                Text.literal(renderedText).setStyle(Style.EMPTY.withFont(HUD_FONT)),
                x + (spriteWidth / 2) - (textWidth / 2),
                y + (spriteHeight / 2) - (fontHeight / 2) + 1,
                0xFFFFFFFF,
                false
        );
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void renderCustomStatusBars(DrawContext context, CallbackInfo ci) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        ci.cancel();

        if (playerEntity == null) {
            return;
        }

        int lastHealthValue = MathHelper.ceil(playerEntity.getHealth());
        long tickTime = Util.getMeasuringTimeMs();

        if (lastHealthValue < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = tickTime;
            this.heartJumpEndTick = this.ticks + 20;
        } else if (lastHealthValue > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = tickTime;
            this.heartJumpEndTick = this.ticks + 10;
        }

        if (tickTime - this.lastHealthCheckTime > 1000L) {
            this.renderHealthValue = lastHealthValue;
            this.lastHealthCheckTime = tickTime;
        }

        this.lastHealthValue = lastHealthValue;
        int health = this.renderHealthValue;
        this.random.setSeed(this.ticks * 312871L);
        int healthBarX = this.scaledWidth / 2 - 90;
        int manaBarX = this.scaledWidth / 2 + 4;
        int statusBarsStartY = this.scaledHeight - 39;
        float maxHealth = Math.max((float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float) Math.max(health, lastHealthValue));
        int shieldAmount = MathHelper.ceil(playerEntity.getAbsorptionAmount());

        final var manaComponent = ModComponents.MANA.get(playerEntity);

        final int maxMana = manaComponent.getMax(playerEntity);
        final int mana = manaComponent.getCurrent();

        this.renderCustomHealth(context, healthBarX, statusBarsStartY, maxHealth, playerEntity.getHealth(), shieldAmount);
        this.renderCustomMana(context, manaBarX, statusBarsStartY, maxMana, mana);
        final int slotSpriteSize = 20;
        final int offset = 5;
        renderSelectedSpell(context, (this.scaledWidth / 2) - 90, this.scaledHeight - 39 - slotSpriteSize - offset, slotSpriteSize);
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderCustomExperience(DrawContext context, int x, CallbackInfo ci) {
        this.client.getProfiler().push("expBar");
        assert this.client.player != null;
        int maxExp = this.client.player.getNextLevelExperience();
        int realY = this.scaledHeight - 27;
        if (maxExp > 0) {
            int spriteWidth = 180;
            int percentagePixels = (int) (this.client.player.experienceProgress * (spriteWidth - 2));
            int spriteHeight = 5;
            int textureWidth = 178;
            int textureHeight = 5;

            context.drawTexture(EXP_BACK_GROUND, x + 1, realY, spriteWidth, spriteHeight, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
            context.setShaderColor(128 / 255f, 255 / 255f, 32 / 255f, 1);
            context.drawTexture(EXP_FILL, x + 2, realY + 1, percentagePixels, spriteHeight - 2, 0, 0, percentagePixels, textureHeight - 2, textureWidth - 2, textureHeight - 2);
            context.setShaderColor(1, 1, 1, 1);
        }

        this.client.getProfiler().pop();
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");
            String string = this.client.player.experienceLevel + "";
            int levelX = (this.scaledWidth - this.getTextRenderer().getWidth(string)) / 2;
            int levelY = this.scaledHeight - 28;
            context.drawText(this.getTextRenderer(), Text.literal(string).setStyle(Style.EMPTY.withFont(HUD_FONT)), levelX + 1, levelY, 0, false);
            context.drawText(this.getTextRenderer(), Text.literal(string).setStyle(Style.EMPTY.withFont(HUD_FONT)), levelX - 1, levelY, 0, false);
            context.drawText(this.getTextRenderer(), Text.literal(string).setStyle(Style.EMPTY.withFont(HUD_FONT)), levelX, levelY + 1, 0, false);
            context.drawText(this.getTextRenderer(), Text.literal(string).setStyle(Style.EMPTY.withFont(HUD_FONT)), levelX, levelY - 1, 0, false);
            context.drawText(this.getTextRenderer(), Text.literal(string).setStyle(Style.EMPTY.withFont(HUD_FONT)), levelX, levelY, 8453920, false);
            this.client.getProfiler().pop();
        }
        ci.cancel();
    }

    @Unique
    private void renderItem(DrawContext context, int slot, int x, int y) {
        assert client.player != null;
        renderHotbarItem(context, x, y, 0, client.player, client.player.getInventory().getStack(slot), 0);
    }

    @Unique
    private void renderItem(DrawContext context, ItemStack stack, int x, int y) {
        renderHotbarItem(context, x, y, 0, client.player, stack, 0);
    }

    @Unique
    private void renderExpensiveItem(DrawContext context, ItemStack stack, int x, int y) {
        renderHotbarItem(context, x, y, 0, client.player, stack, 0);
        context.fill(x, y, x + 16, y + 16, 0xAA_CC3333);
    }

}
