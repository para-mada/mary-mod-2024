package com.paramada.marycum2024.items;

import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.blocks.BlockManager;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroups {
    public static ItemGroup MARY_MOD_BLOCKS_GROUP;
    public static ItemGroup MARY_MOD_ARMORS_GROUP;
    public static ItemGroup MARY_MOD_MISC_GROUP;
    public static ItemGroup MARY_MOD_ACCESORIES_GROUP;
    public static ItemGroup MARY_MOD_WEAPONS_GROUP;
    public static ItemGroup MARY_MOD_SPELLS_GROUP;
    public static ItemGroup MARY_MOD_HEALING_GROUP;
    public static ItemGroup MARY_MOD_CONTAINERS_GROUP;

    public static void registerItemGroups() {
        MARY_MOD_BLOCKS_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_blocks"),
                FabricItemGroup.builder().icon(() -> new ItemStack(BlockManager.SHULKER_BLOCK))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_blocks"))
                        .entries(((displayContext, entries) -> {
                            entries.add(BlockManager.EFIGY);
                            entries.add(BlockManager.SHULKER_BLOCK);
                        })).build()
        );
        MARY_MOD_ARMORS_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_armors"),
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemManager.MARY_COIN))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_armors"))
                        .entries(((displayContext, entries) -> {
                        })).build()
        );
        MARY_MOD_CONTAINERS_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_containers"),
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemManager.POTION_CASE))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_containers"))
                        .entries(((displayContext, entries) -> {
                            entries.add(ItemManager.POTION_CASE);
                            entries.add(ItemManager.POTION_CASE_2);
                        })).build()
        );
        MARY_MOD_MISC_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_misc"),
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemManager.MARY_COIN))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_misc"))
                        .entries(((displayContext, entries) -> {
                            entries.add(ItemManager.MARY_COIN);
                            entries.add(ItemManager.MARY_GOLD);
                            entries.add(ItemManager.MARY_SILVER);
                            entries.add(ItemManager.MARY_COPPER);
                            entries.add(ItemManager.BEAGLE_SPAWN_EGG);
                            entries.add(ItemManager.TDAH_PILL);

                            entries.add(ItemManager.PINK_RIBBON);

                        })).build()
        );
        MARY_MOD_ACCESORIES_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_accesories"),
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemManager.MICROPHONE_TRINKET))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_accesories"))
                        .entries(((displayContext, entries) -> {
                            entries.add(ItemManager.GLASSES);
                            entries.add(ItemManager.BLACK_RIBBON_TRINKET);
                            entries.add(ItemManager.BLUE_RIBBON_TRINKET);
                            entries.add(ItemManager.CYAN_RIBBON_TRINKET);
                            entries.add(ItemManager.GREEN_RIBBON_TRINKET);
                            entries.add(ItemManager.PINK_RIBBON_TRINKET);
                            entries.add(ItemManager.RED_RIBBON_TRINKET);
                            entries.add(ItemManager.MICROPHONE_TRINKET);
                            entries.add(ItemManager.MANA_NECKLACE);
                            entries.add(ItemManager.MANA_RING);
                            entries.add(ItemManager.ARCANE_RING);
                            entries.add(ItemManager.MANA_BRACELET);
                            entries.add(ItemManager.GOLD_BRACELET);
                        })).build()
        );
        MARY_MOD_SPELLS_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_spells"),
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemManager.REGEN_SPELL))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_spells"))
                        .entries(((displayContext, entries) -> {
                            entries.add(ItemManager.HEALING_SPELL);
                            entries.add(ItemManager.MIDAS_SPELL);
                            entries.add(ItemManager.REGEN_SPELL);
                            entries.add(ItemManager.CURSED_SHADOW);
                            entries.add(ItemManager.TAUNT_SKILL);
                            entries.add(ItemManager.HOLY_LIGHT);
                            entries.add(ItemManager.MANA_SHIELD);
                            entries.add(ItemManager.THUNDER_STORM);

                        })).build()
        );
        MARY_MOD_WEAPONS_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_weapons"),
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemManager.BLOOD_DAGGER))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_weapons"))
                        .entries(((displayContext, entries) -> {
                            entries.add(ItemManager.MYTHICAL_STAFF);
                            entries.add(ItemManager.BLOOD_DAGGER);

                            entries.add(ItemManager.PRACTICE_BOH_STAFF);
                            entries.add(ItemManager.COMBAT_BOH_STAFF);
                            entries.add(ItemManager.ARMORED_BOH_STAFF);
                            entries.add(ItemManager.SUBLIME_BOH_STAFF);

                            entries.add(ItemManager.COMMON_SHIELD);
                            entries.add(ItemManager.UNCOMMON_SHIELD);
                            entries.add(ItemManager.RARE_SHIELD);
                            entries.add(ItemManager.EPIC_SHIELD);
                            entries.add(ItemManager.LEGENDARY_SHIELD);

                        })).build()
        );
        MARY_MOD_HEALING_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MaryMod2024.MOD_ID, "mary_mod_healing"),
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemManager.BANDAGE))
                        .displayName(Text.translatable("itemGroup.mary-mod-2024.mary_mod_healing"))
                        .entries(((displayContext, entries) -> {

                            entries.add(ItemManager.BANDAGE);

                            entries.add(ItemManager.MEDIKA_POTION);
                            entries.add(ItemManager.ESTUS);
                        })).build()
        );
    }
}
