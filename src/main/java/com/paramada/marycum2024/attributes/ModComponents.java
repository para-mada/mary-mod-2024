package com.paramada.marycum2024.attributes;

import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.attributes.mana.ManaComponent;
import com.paramada.marycum2024.attributes.shield.ShieldComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

public final class ModComponents {
    public static final ComponentKey<ManaComponent> MANA = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MaryMod2024.MOD_ID, "mana"), ManaComponent.class);
    public static final ComponentKey<ShieldComponent> SHIELD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MaryMod2024.MOD_ID, "shield"), ShieldComponent.class);
}

