package com.paramada.marycum2024.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.UUID;

public final class ManaShield {
    public final UUID owner;              // puede ser null si lo coloca el servidor o un bloque
    public final ServerWorld world;
    public Vec3d center;                  // si sigue a un entity, se actualiza cada tick
    public final int radius;              // radio en bloques
    public final double shellThickness;   // grosor de la cáscara (colisión)
    public long expiresAt;                // world.getTime() en ticks
    public boolean followEntity;          // true si debe tomar la pos de un entity
    public @Nullable WeakReference<LivingEntity> anchor; // entidad a seguir

    // opcional: “HP” del escudo
    public int capacity;                  // si 0, escudo cae

    public ManaShield(UUID owner, ServerWorld world, Vec3d center, int radius, double shellThickness, long durationTicks) {
        this.owner = owner;
        this.world = world;
        this.center = center;
        this.radius = radius;
        this.shellThickness = shellThickness;
        this.expiresAt = world.getTime() + durationTicks;
        this.followEntity = false;
        this.anchor = null;
        this.capacity = 0;
    }

    public boolean isExpired(long now) { return now >= expiresAt || capacity < 0; }

    public double dist2(Vec3d p) { return p.squaredDistanceTo(center); }

    public boolean inside(Vec3d p) { return dist2(p) <= (radius * radius); }

    // sobre la cáscara (con grosor)
    public boolean onShell(Vec3d p) {
        double d = Math.sqrt(dist2(p));
        return d >= radius - shellThickness && d <= radius + shellThickness;
    }
}
