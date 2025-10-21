package com.paramada.marycum2024.util.functionality;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Sched: ejecuta runnables en ticks futuros.
public final class TickScheduler {
    private static final Map<MinecraftServer, Long2ObjectMap<List<Runnable>>> QUEUE = new Object2ObjectOpenHashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            var byTick = QUEUE.computeIfAbsent(server, s -> new Long2ObjectOpenHashMap<>());
            long now = server.getOverworld().getTime(); // o por-world si prefieres
            var list = byTick.remove(now);
            if (list != null) list.forEach(Runnable::run);
        });
    }

    public static void schedule(ServerWorld world, int delayTicks, Runnable r) {
        var byTick = QUEUE.computeIfAbsent(world.getServer(), s -> new Long2ObjectOpenHashMap<>());
        long due = world.getTime() + Math.max(0, delayTicks);
        byTick.computeIfAbsent(due, t -> new ArrayList<>()).add(r);
    }
}

