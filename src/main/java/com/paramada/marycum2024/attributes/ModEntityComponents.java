package com.paramada.marycum2024.attributes;

import com.paramada.marycum2024.attributes.mana.ManaComponentImpl;
import com.paramada.marycum2024.attributes.shield.ShieldComponentImpl;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public final class ModEntityComponents implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ModComponents.MANA, player -> new ManaComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(ModComponents.SHIELD, player -> new ShieldComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
