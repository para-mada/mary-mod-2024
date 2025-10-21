package com.paramada.marycum2024.entities;

import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.entities.custom.BeagleEntity;
import com.paramada.marycum2024.entities.projectiles.SpellProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BeagleEntity> BEAGLE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MaryMod2024.MOD_ID, "beagle"),
            EntityType.Builder.create(BeagleEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(1, 1)
                    .build()
    );

    public static final EntityType<SpellProjectileEntity> SPELL_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MaryMod2024.MOD_ID, "spell_projectile"),
            EntityType.Builder.<SpellProjectileEntity>create(SpellProjectileEntity::new, SpawnGroup.MISC)
                    .setDimensions(0.5f, 0.5f).build()
    );

    public static final EntityType<HolyLightSpellEntity> HOLY_LIGHT_SPELL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MaryMod2024.MOD_ID, "holy_light_spell"),
            EntityType.Builder.create(HolyLightSpellEntity::new, SpawnGroup.MISC)
                    .disableSaving()
                    .disableSummon()
                    .setDimensions(0f, 0f).build()
    );
}
