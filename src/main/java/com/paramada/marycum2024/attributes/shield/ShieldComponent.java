package com.paramada.marycum2024.attributes.shield;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ShieldComponent extends Component {
    int getCurrent();
    int getMax(PlayerEntity player);

    /** Intenta gastar; true si alcanzó */
    boolean tryConsume(int amount);

    /** Rellena hasta el tope */
    void add(int amount);

    /** Lógica por tick (regen) */
    void tick(ServerPlayerEntity player);

    /** Forzar sync al cliente */
    void sync(ServerPlayerEntity player);
}

