package com.paramada.marycum2024.spells;

import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// CLIENT: almacén de shields visibles
public final class ClientShields {
    public static final Map<UUID, ClientShield> ACTIVE = new ConcurrentHashMap<>();

    public static final class ClientShield {
        public final UUID id;
        public Vec3d center;
        public float radius;
        public long expiresAt; // en ticks de mundo (aprox), para limpiar
        public ClientShield(UUID id, Vec3d c, float r, long ex) { this.id=id; center=c; radius=r; expiresAt=ex; }
    }
}

