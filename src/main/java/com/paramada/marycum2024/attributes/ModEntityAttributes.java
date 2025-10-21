package com.paramada.marycum2024.attributes;

import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.attributes.bases.SimpleEntityAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntityAttributes {

    /// Offensive Atts
    public static final EntityAttribute DISTANCE_DAMAGE = new SimpleEntityAttribute("attribute.name.generic.distance_damage", 1).setTracked(true);
    public static final EntityAttribute MAGIC_DAMAGE = new SimpleEntityAttribute("attribute.name.generic.magic_damage", 1).setTracked(true);

    /// Defensive Atts
    public static final EntityAttribute PHYSICAL_RESISTANCE = new SimpleEntityAttribute("attribute.name.generic.physical_resistance", 0).setTracked(true);
    public static final EntityAttribute MAGICAL_RESISTANCE = new SimpleEntityAttribute("attribute.name.generic.magical_resistance", 0).setTracked(true);
    public static final EntityAttribute DODGE_CHANCE = new ClampedEntityAttribute("attribute.name.generic.dodge_chance", 0, 0, 100).setTracked(true);
    /// Misc Stats
    public static final EntityAttribute HEAL_EFFICIENCY = new SimpleEntityAttribute("attribute.name.generic.heal_efficiency", 1).setTracked(true);
    public static final EntityAttribute COOLDOWN_REDUCTION = new ClampedEntityAttribute("attribute.name.generic.cooldown_reduction", 0, 0, 100).setTracked(true);
    public static final EntityAttribute MANA_REGENERATION = new SimpleEntityAttribute("attribute.name.generic.mana_regeneration", 0).setTracked(true);
    public static final EntityAttribute ADDITIONAL_MANA = new SimpleEntityAttribute("attribute.name.generic.additional_mana", 0).setTracked(true);

    ///  Builder
    public static final DefaultAttributeContainer.Builder playerAttributes = PlayerEntity.createPlayerAttributes();

    private static void registerAttribute(final String name, final EntityAttribute entityAttribute) {
        Registry.register(Registries.ATTRIBUTE, new Identifier(MaryMod2024.MOD_ID, name), entityAttribute);
        FabricDefaultAttributeRegistry.register(EntityType.PLAYER, playerAttributes.add(entityAttribute));
    }
    public static void registerAttributes() {
        registerAttribute("generic.distance_damage", DISTANCE_DAMAGE);
        registerAttribute("generic.magic_damage", MAGIC_DAMAGE);

        registerAttribute("generic.physical_resistance", PHYSICAL_RESISTANCE);
        registerAttribute("generic.magical_resistance", MAGICAL_RESISTANCE);
        registerAttribute("generic.dodge_chance", DODGE_CHANCE);

        registerAttribute("generic.cooldown_reduction", COOLDOWN_REDUCTION);
        registerAttribute("generic.mana_regeneration", MANA_REGENERATION);
        registerAttribute("generic.additional_mana", ADDITIONAL_MANA);
        registerAttribute("generic.heal_efficiency", HEAL_EFFICIENCY);
    }
}
