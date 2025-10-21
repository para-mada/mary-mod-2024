package com.paramada.marycum2024.attributes.mana;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ManaComponent extends Component {
    int getCurrent();
    int getMax();
    int getMax(PlayerEntity player);
    int getRegenPerSecond();
    int getRegenPerSecond(PlayerEntity player);

    void setCurrent(int v);
    void setMax(int v);

    /** Intenta gastar; true si alcanzó */
    boolean tryConsume(int amount);

    /** Rellena hasta el tope */
    void restore(int amount);

    /** Lógica por tick (regen) */
    void tick(ServerPlayerEntity player);

    /** Forzar sync al cliente */
    void sync(ServerPlayerEntity player);
}

