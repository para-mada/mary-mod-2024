package com.paramada.marycum2024.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.blocks.custom.entities.BlockEntityManager;
import com.paramada.marycum2024.blocks.custom.entities.renderers.EfigyBlockEntityRenderer;
import com.paramada.marycum2024.blocks.custom.entities.renderers.HiddenBlockEntityRenderer;
import com.paramada.marycum2024.entities.ModEntities;
import com.paramada.marycum2024.entities.client.BeagleModel;
import com.paramada.marycum2024.entities.client.BeagleRenderer;
import com.paramada.marycum2024.entities.client.ModModelLayers;
import com.paramada.marycum2024.events.KeyboardHandler;
import com.paramada.marycum2024.items.ItemManager;
import com.paramada.marycum2024.items.custom.weapons.GuardianShield;
import com.paramada.marycum2024.networking.NetworkManager;
import com.paramada.marycum2024.screens.EfigyScreen;
import com.paramada.marycum2024.screens.ParticularContainerScreen;
import com.paramada.marycum2024.screens.handlers.ModScreenHandlers;
import com.paramada.marycum2024.spells.ClientShields;
import com.paramada.marycum2024.util.functionality.SpellCastHelper;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import com.paramada.marycum2024.util.functionality.bridges.PlayerEntityBridge;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class MaryMod2024Client implements ClientModInitializer {

    private static final Identifier CHANNELING_PRED_ID = new Identifier(MaryMod2024.MOD_ID, "channeling");
    private static final ClampedModelPredicateProvider CHANNELING_PRED = (stack, world, entity, seed) -> {
        if (entity == null) return 0.0f;
        // Usa tu flag/NBT y verifica que realmente lo esté usando en MAIN_HAND
        boolean nbtFlag = GuardianShield.isChanneling(stack);
        boolean strict = entity.isUsingItem() && entity.getActiveItem() == stack;
        return (nbtFlag && strict) ? 1.0f : 0.0f;
    };

    @Override
    public void onInitializeClient() {
        NetworkManager.registerS2CPackets();
        KeyboardHandler.register();

        EntityRendererRegistry.register(ModEntities.BEAGLE, BeagleRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BEAGLE, BeagleModel::getTexturedModelData);

        HandledScreens.register(ModScreenHandlers.EFIGY_BLOCK_SCREEN_HANDLER, EfigyScreen::new);

        HandledScreens.register(ModScreenHandlers.PARTICULAR_SCREEN_HANDLER, ParticularContainerScreen::new);

        BlockEntityRendererFactories.register(BlockEntityManager.EFIGY_ENTITY, EfigyBlockEntityRenderer::new);

        BlockEntityRendererFactories.register(BlockEntityManager.HIDDEN_ENTITY, HiddenBlockEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SPELL_PROJECTILE, FlyingItemEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.HOLY_LIGHT_SPELL, EmptyEntityRenderer::new);

        ModelPredicateProviderRegistry.register(ItemManager.COMMON_SHIELD, CHANNELING_PRED_ID, CHANNELING_PRED);
        ModelPredicateProviderRegistry.register(ItemManager.UNCOMMON_SHIELD, CHANNELING_PRED_ID, CHANNELING_PRED);
        ModelPredicateProviderRegistry.register(ItemManager.RARE_SHIELD, CHANNELING_PRED_ID, CHANNELING_PRED);
        ModelPredicateProviderRegistry.register(ItemManager.EPIC_SHIELD, CHANNELING_PRED_ID, CHANNELING_PRED);
        ModelPredicateProviderRegistry.register(ItemManager.LEGENDARY_SHIELD, CHANNELING_PRED_ID, CHANNELING_PRED);

        ClientPlayConnectionEvents.JOIN.register((a, b, client) -> {
            ClientPlayNetworking.send(NetworkManager.REQUEST_UPGRADE_ID, PacketByteBufs.create());
            ClientPlayNetworking.send(NetworkManager.REQUEST_MONEY_ID, PacketByteBufs.create());
            ClientPlayNetworking.send(NetworkManager.REQUEST_LEVEL_ID, PacketByteBufs.create());
            MinecraftClient.getInstance().options.getDamageTiltStrength().setValue(0d);
        });

        WorldRenderEvents.END.register(ctx -> {
            if (!SpellPreviewState.active || SpellPreviewState.center == null) return;
            SpellCastHelper.showAreaPreview(ctx, SpellPreviewState.center, SpellPreviewState.radius, 0);
        });

        WorldRenderEvents.AFTER_ENTITIES.register((ctx) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null) return;

            // matriz base
            MatrixStack matrices = ctx.matrixStack();
            Camera cam = ctx.camera();
            Vec3d camPos = cam.getPos();

            long now = mc.world.getTime();
            // limpiar expirados
            ClientShields.ACTIVE.values().removeIf(s -> s.expiresAt <= now);

            // prepara buffer para triángulos translúcidos
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            // shader/estado
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.depthMask(true);

            // cúpula: esfera UV con alpha
            for (ClientShields.ClientShield s : ClientShields.ACTIVE.values()) {
                // frustum culling simple (opcional: usa ctx.frustum().isVisible)
                if (s.center.squaredDistanceTo(camPos) > (s.radius + 64) * (s.radius + 64)) continue;

                // mover a espacio de cámara
                matrices.push();
                matrices.translate(camPos.x, camPos.y, camPos.z);

                drawTranslucentSphere(matrices, buf, (int) Math.max(16, Math.min(36, s.radius * 4)), (int) Math.max(24, Math.min(64, s.radius * 6)), s.radius, 129);

                // contorno fino opcional
                drawSphereWireframe(matrices, buf, 24, 48, s.radius, 1f);

                matrices.pop();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        });
    }

    private static void drawTranslucentSphere(MatrixStack matrices, BufferBuilder buf, int latSteps, int lonSteps, float r, int alpha) {
        Matrix4f mat = matrices.peek().getPositionMatrix();

        // Triángulos
        buf.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        int a = alpha;
        int cr = 44, cg = 156, cb = 224; // azul/cian suave (puedes parametrizarlo)

        for (int i = 0; i < latSteps; i++) {
            double t0 = Math.PI * i / latSteps;
            double t1 = Math.PI * (i + 1) / latSteps;
            float y0 = (float) (r * Math.cos(t0));
            float y1 = (float) (r * Math.cos(t1));
            float ring0 = (float) (r * Math.sin(t0));
            float ring1 = (float) (r * Math.sin(t1));

            for (int j = 0; j < lonSteps; j++) {
                double p0 = 2 * Math.PI * j / lonSteps;
                double p1 = 2 * Math.PI * (j + 1) / lonSteps;

                float x00 = (float) (ring0 * Math.cos(p0));
                float z00 = (float) (ring0 * Math.sin(p0));
                float x01 = (float) (ring0 * Math.cos(p1));
                float z01 = (float) (ring0 * Math.sin(p1));
                float x10 = (float) (ring1 * Math.cos(p0));
                float z10 = (float) (ring1 * Math.sin(p0));
                float x11 = (float) (ring1 * Math.cos(p1));
                float z11 = (float) (ring1 * Math.sin(p1));

                // quad -> 2 triángulos
                buf.vertex(mat, x00, y0, z00).color(cr, cg, cb, a).next();
                buf.vertex(mat, x10, y1, z10).color(cr, cg, cb, a).next();
                buf.vertex(mat, x11, y1, z11).color(cr, cg, cb, a).next();

                buf.vertex(mat, x00, y0, z00).color(cr, cg, cb, a).next();
                buf.vertex(mat, x11, y1, z11).color(cr, cg, cb, a).next();
                buf.vertex(mat, x01, y0, z01).color(cr, cg, cb, a).next();
            }
        }
        BufferRenderer.drawWithGlobalProgram(buf.end());
    }

    private static void drawSphereWireframe(MatrixStack matrices, BufferBuilder buf, int latSteps, int lonSteps, float r, float alpha) {
        Matrix4f mat = matrices.peek().getPositionMatrix();
        buf.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        int a = (int) (alpha * 255), cr = 71, cg = 152, cb = 202;

        // latitudes
        for (int i = 1; i < latSteps; i++) {
            double t = Math.PI * i / latSteps;
            float y = (float) (r * Math.cos(t));
            float ring = (float) (r * Math.sin(t));
            float prevx = (float) (ring * Math.cos(0));
            float prevz = (float) (ring * Math.sin(0));
            for (int j = 1; j <= lonSteps; j++) {
                double p = 2 * Math.PI * j / lonSteps;
                float x = (float) (ring * Math.cos(p));
                float z = (float) (ring * Math.sin(p));
                buf.vertex(mat, prevx, y, prevz).color(cr, cg, cb, a).next();
                buf.vertex(mat, x, y, z).color(cr, cg, cb, a).next();
                prevx = x;
                prevz = z;
            }
        }

        // meridianos
        for (int j = 0; j < lonSteps; j += Math.max(1, lonSteps / 12)) {
            double p = 2 * Math.PI * j / lonSteps;
            float prevx = (float) (r * Math.sin(0) * Math.cos(p));
            float prevy = (float) (r * Math.cos(0));
            float prevz = (float) (r * Math.sin(0) * Math.sin(p));
            for (int i = 1; i <= latSteps; i++) {
                double t = Math.PI * i / latSteps;
                float x = (float) (r * Math.sin(t) * Math.cos(p));
                float y = (float) (r * Math.cos(t));
                float z = (float) (r * Math.sin(t) * Math.sin(p));
                buf.vertex(mat, prevx, prevy, prevz).color(cr, cg, cb, a).next();
                buf.vertex(mat, x, y, z).color(cr, cg, cb, a).next();
                prevx = x;
                prevy = y;
                prevz = z;
            }
        }
        BufferRenderer.drawWithGlobalProgram(buf.end());
    }

}
/*

nada de chistes fisicos
nada de odio a novela
se permite el shansi
se permite cherry
esperancita peruana prohibida
streams de cocina permitidos
danger tentativo ** TERRY
contador de muertes
*/
