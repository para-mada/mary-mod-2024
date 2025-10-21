package com.paramada.marycum2024.util.functionality;

import com.paramada.marycum2024.MaryMod2024;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

// Utils: obtener el punto de impacto (o un punto a distancia 'range' si no pega)
public final class SpellCastHelper {

    public static final Identifier AOE_TEX = new Identifier(MaryMod2024.MOD_ID, "textures/hud/spell/round_area.png");

    public static BlockHitResult raycastBlockOnly(PlayerEntity player, double range, float tickDelta, boolean hitFluids) {
        Vec3d start = player.getCameraPosVec(tickDelta);
        Vec3d look = player.getRotationVec(tickDelta);
        Vec3d end = start.add(look.multiply(range));

        RaycastContext.FluidHandling fluids = hitFluids
                ? RaycastContext.FluidHandling.ANY
                : RaycastContext.FluidHandling.NONE;

        // IMPORTANTE: world.raycast con RaycastContext devuelve SOLO bloque (BlockHitResult).
        return player.getWorld().raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                fluids,
                player // se usa para colisiones, pero entidades NO se consideran en este raycast
        ));
    }

    // Si no golpea nada, podemos “proyectar” al plano del suelo o al y-top más alto
    public static Vec3d fallbackToGround(World world, Vec3d start, Vec3d end) {
        // usa x/z del punto final y busca la altura del terreno (evita cuevas abiertas)
        BlockPos col = BlockPos.ofFloored(end.x, world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) Math.floor(end.x), (int) Math.floor(end.z)), end.z);
        return new Vec3d(col.getX() + 0.5, col.getY() + 0.01, col.getZ() + 0.5);
    }

    public static Vec3d spellRayCast(World world, PlayerEntity player, double range, float tickDelta, boolean hitFluids) {
        final BlockHitResult hit = SpellCastHelper.raycastBlockOnly(player, range, tickDelta, hitFluids);

        if (hit.getType() != HitResult.Type.BLOCK) {
            Vec3d start = player.getCameraPosVec(0f);
            Vec3d look = player.getRotationVec(0f);
            Vec3d end = start.add(look.multiply(range));
            return SpellCastHelper.fallbackToGround(world, start, end);
        }

        if (hit.getSide() == Direction.UP) {
            return hit.getPos();
        }

        BlockPos bp = hit.getBlockPos();
        return new Vec3d(bp.getX() + 0.5, bp.getY() + 1.01, bp.getZ() + 0.5);

    }

    public static void spawnCirclePreview(World world, Vec3d center, double radius, int segments, double yOffset) {
        double y = center.y + yOffset;
        for (int i = 0; i < segments; i++) {
            double t1 = (2 * Math.PI) * i / segments;
            double x = center.x + radius * Math.cos(t1);
            double z = center.z + radius * Math.sin(t1);
            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    public static void spawnCircleArea(World world, Vec3d center, double radius, int segments, double yOffset) {
        double y = center.y + yOffset;
        for (int i = 0; i < segments; i++) {
            double t1 = (2 * Math.PI) * i / segments;
            double x = center.x + radius * Math.cos(t1);
            double z = center.z + radius * Math.sin(t1);
            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    public static void showAreaPreview(WorldRenderContext ctx, Vec3d center, double radius, double yOffset) {
        if (center == null || radius <= 0) return;

        var mc = MinecraftClient.getInstance();
        var vcp = mc.getBufferBuilders().getEntityVertexConsumers();
        // Translucente, con depth-test normal
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityTranslucent(AOE_TEX));

        MatrixStack ms = ctx.matrixStack();
        Camera camera = ctx.camera();
        Vec3d cam = camera.getPos();

        ms.push();
        // Mover a coordenadas del mundo relativas a la cámara
        ms.translate(center.x - cam.x, center.y - cam.y + 0.06, center.z - cam.z);

        // Rotar para que quede horizontal (plano XZ). Dependiendo de mapeo:
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

        // Escala: el quad base mide 1x1 → lo escalamos a (2*radius)
        float size = (float)(2.0 * radius);
        ms.scale(size, size, 1f);

        // Quad centrado en el origen: [-0.5, 0.5] × [-0.5, 0.5]
        MatrixStack.Entry e = ms.peek();
        Matrix4f m = e.getPositionMatrix();
        Matrix3f n = e.getNormalMatrix();

        // Color/alpha multiplicativos sobre la textura
        addVertex(vc, m, n, -0.5f,  0.5f, 0f, 0f, 0f, 1,1,1,1f); // TL (u=0,v=0)
        addVertex(vc, m, n,  0.5f,  0.5f, 0f, 1f, 0f, 1,1,1,1f); // TR (u=1,v=0)
        addVertex(vc, m, n,  0.5f, -0.5f, 0f, 1f, 1f, 1,1,1,1f); // BR (u=1,v=1)
        addVertex(vc, m, n, -0.5f, -0.5f, 0f, 0f, 1f, 1,1,1,1f); // BL (u=0,v=1)

        ms.pop();
        vcp.draw();
    }

    private static void addVertex(VertexConsumer vc, Matrix4f m, Matrix3f n,
                                  float x, float y, float z, float u, float v,
                                  float r, float g, float b, float a) {
        vc.vertex(m, x, y, z)
                .color(r, g, b, a)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE) // = full bright
                .normal(n, 0, 1, 0)
                .next();
    }

    public static void summonLightning(Vec3d originPos, PlayerEntity player, World world) {
        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
        assert lightningEntity != null;
        lightningEntity.refreshPositionAfterTeleport(originPos);
        lightningEntity.setChanneler((ServerPlayerEntity) player);
        world.spawnEntity(lightningEntity);
        player.playSound(SoundEvents.ITEM_TRIDENT_THUNDER, 5.0F, 1.0F);
    }
}
