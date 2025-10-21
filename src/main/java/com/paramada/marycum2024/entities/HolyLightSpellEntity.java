package com.paramada.marycum2024.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.joml.Vector3f;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class HolyLightSpellEntity extends Entity {

    // Config
    private static final int TELEGRAPH_TICKS = 30; // círculo que se encoge
    private static final int BEAM_TICKS = 20;       // “rayo”
    private static final float START_RADIUS = 5.0f;
    private static final float END_RADIUS = 0.8f;
    private static final int BEAM_HEIGHT = 48;
    private Runnable handler;

    private int ageTicks = 0;
    private boolean beamSoundPlayed = false;
    private boolean startSoundPlayed = false;

    public HolyLightSpellEntity(EntityType<? extends HolyLightSpellEntity> type, World world) {
        super(type, world);
        this.noClip = true;
        this.handler = null;
    }

    public static void spawn(ServerWorld world, Vec3d pos, net.minecraft.entity.LivingEntity owner, Runnable handler) {
        HolyLightSpellEntity entity = Objects.requireNonNull(ModEntities.HOLY_LIGHT_SPELL.create(world));
        entity.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0, 0);
        entity.handler = handler;
        world.spawnEntity(entity);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) return;

        ServerWorld sw = (ServerWorld) this.getWorld();
        Vec3d center = this.getPos();

        if (ageTicks < TELEGRAPH_TICKS) {
            if (!startSoundPlayed) {
                sw.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                startSoundPlayed = true;
            }
            // FASE A: círculo de polvo encogiéndose
            float t = (ageTicks) / (float) TELEGRAPH_TICKS;
            float radius = lerp(START_RADIUS, END_RADIUS, t);
            spawnRing(sw, center, radius, 64, 0.0f);
        } else if (ageTicks < TELEGRAPH_TICKS + BEAM_TICKS) {
            // FASE B: rayo vertical con partículas + sonido al entrar
            if (!beamSoundPlayed) {
                sw.playSound(null, center.x, center.y, center.z, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                beamSoundPlayed = true;
                handler.run();
            }
            spawnBeam(sw, center, BEAM_HEIGHT);
        } else {
            this.discard(); // fin
        }

        ageTicks++;
    }

    private void spawnRing(ServerWorld world, Vec3d center, float radius, int points, float yOffset) {
        // Color “holy”: dorado/blanco suave (DustParticleEffect)
        // DustParticleEffect toma un Vector3f RGB [0..1] + scale
        DustParticleEffect holy = new DustParticleEffect(new Vector3f(231/255f, 232/255f, 167/255f), 1.0f);

        double y = center.y + yOffset;
        for (int i = 0; i < points; i++) {
            double theta = (i / (double) points) * Math.PI * 2.0;
            double x = center.x + radius * Math.cos(theta);
            double z = center.z + radius * Math.sin(theta);
            // un poco de “polvo” ascendente random
            double vx = 0.0;
            double vy = 0.01 + world.getRandom().nextDouble() * 0.02;
            double vz = 0.0;
            world.spawnParticles(holy, x, y, z, 1, 0, 0, 0, 0);
            // ligeros “sparkles”
            if (world.getRandom().nextFloat() < 0.1f) {
                world.spawnParticles(ParticleTypes.END_ROD, x, y + 0.1, z, 1, 0, 0, 0, 0.0);
            }
        }
    }

    private void spawnBeam(ServerWorld world, Vec3d base, int height) {
        // MVP: columna densa de END_ROD + alguna DUST clara (simula beacon)
        for (int y = 0; y < height; y++) {
            double px = base.x + (world.getRandom().nextDouble() - 0.5) * 0.2;
            double pz = base.z + (world.getRandom().nextDouble() - 0.5) * 0.2;
            double py = base.y + y;
            world.spawnParticles(ParticleTypes.END_ROD, px, py, pz, 6, 0.02, 0.02, 0.02, 0.0);
            if (y % 4 == 0) {
                world.spawnParticles(new DustParticleEffect(new Vector3f(1.0f, 1.0f, 0.4f), 1.0f), px, py, pz, 4, 0.01, 0.01, 0.01, 0.0);
            }
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        return EntityDimensions.fixed(0f, 0f);
    }
}
