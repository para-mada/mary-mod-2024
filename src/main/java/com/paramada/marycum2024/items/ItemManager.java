package com.paramada.marycum2024.items;

import com.google.common.collect.ImmutableMultimap;
import com.paramada.marycum2024.MaryMod2024;
import com.paramada.marycum2024.attributes.ModEntityAttributes;
import com.paramada.marycum2024.effects.ModEffects;
import com.paramada.marycum2024.entities.HolyLightSpellEntity;
import com.paramada.marycum2024.entities.ModEntities;
import com.paramada.marycum2024.items.custom.*;
import com.paramada.marycum2024.items.custom.containers.ParticularContainerItem;
import com.paramada.marycum2024.items.custom.containers.PouchPredicates;
import com.paramada.marycum2024.items.custom.potions.Bandage;
import com.paramada.marycum2024.items.custom.potions.MedikaPotion;
import com.paramada.marycum2024.items.custom.potions.ReusablePotion;
import com.paramada.marycum2024.items.custom.weapons.BohStaff;
import com.paramada.marycum2024.items.custom.weapons.Dagger;
import com.paramada.marycum2024.items.custom.weapons.GuardianShield;
import com.paramada.marycum2024.items.custom.weapons.MagicWand;
import com.paramada.marycum2024.items.trinkets.Glasses;
import com.paramada.marycum2024.items.trinkets.MicrophoneTrinket;
import com.paramada.marycum2024.items.trinkets.SpellTrinketBuilder;
import com.paramada.marycum2024.items.trinkets.bases.AccessoryTrinket;
import com.paramada.marycum2024.items.trinkets.bases.RibbonTrinket;
import com.paramada.marycum2024.networking.NetworkManager;
import com.paramada.marycum2024.util.functionality.SpellCastHelper;
import com.paramada.marycum2024.util.functionality.TickScheduler;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;


public class ItemManager {
    public static final Item MARY_COIN = new MaryCoinItem();
    public static final Item MARY_GOLD = new MaryCoin(Rarity.RARE);
    public static final Item MARY_SILVER = new MaryCoin(Rarity.UNCOMMON);
    public static final Item MARY_COPPER = new MaryCoin(Rarity.COMMON);

    public static final Item PINK_RIBBON = new RibbonItem(Rarity.COMMON);
    public static final Item BANDAGE = new Bandage();
    public static final Item ESTUS = new ReusablePotion();
    public static final Item BEAGLE_SPAWN_EGG = registerItem("beagle_spawn_egg", new SpawnEggItem(ModEntities.BEAGLE, 0xFF9c7144, 0xFF2a1a0d, new FabricItemSettings()));
    public static final Item MEDIKA_POTION = new MedikaPotion();
    public static final Item POTION_CASE = new ParticularContainerItem(Rarity.COMMON, 3, 3, PouchPredicates.POTION_PREDICATE);
    public static final Item POTION_CASE_2 = new ParticularContainerItem(Rarity.COMMON, 4, 4, PouchPredicates.POTION_PREDICATE);
    public static final Item TDAH_PILL = new TDAHPillItem();

    /// Weapons

    public static final Item PRACTICE_BOH_STAFF = new BohStaff(ToolMaterials.WOOD, 3, -2.5f);
    public static final Item COMBAT_BOH_STAFF = new BohStaff(ToolMaterials.WOOD, 4, -1f);
    public static final Item ARMORED_BOH_STAFF = new BohStaff(ToolMaterials.WOOD, 7, -2f);
    public static final Item SUBLIME_BOH_STAFF = new BohStaff(ToolMaterials.WOOD, 9, -1.5f);

    public static final Item COMMON_SHIELD = new GuardianShield(
            10,
            -3.5f,
            ItemRarity.COMMON,
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.PHYSICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_physical_resistance", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.MAGICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_magic_resistance", 20, EntityAttributeModifier.Operation.ADDITION)
            ),
            ImmutableMultimap.of(),
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_thoughness", 3, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_knockback", 10, EntityAttributeModifier.Operation.ADDITION)
            )
    );
    public static final Item UNCOMMON_SHIELD = new GuardianShield(
            10,
            -3.4f,
            ItemRarity.UNCOMMON,
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.PHYSICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_physical_resistance", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.MAGICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_magic_resistance", 20, EntityAttributeModifier.Operation.ADDITION)
            ),
            ImmutableMultimap.of(),
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_thoughness", 3, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_knockback", 15, EntityAttributeModifier.Operation.ADDITION)
            )
    );
    public static final Item RARE_SHIELD = new GuardianShield(
            10,
            -3.3f,
            ItemRarity.RARE,
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.PHYSICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_physical_resistance", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.MAGICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_magic_resistance", 20, EntityAttributeModifier.Operation.ADDITION)
            ),
            ImmutableMultimap.of(),
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_thoughness", 3, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_knockback", 20, EntityAttributeModifier.Operation.ADDITION)
            )
    );
    public static final Item EPIC_SHIELD = new GuardianShield(
            10,
            -3.2f,
            ItemRarity.EPIC,
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.PHYSICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_physical_resistance", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.MAGICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_magic_resistance", 20, EntityAttributeModifier.Operation.ADDITION)
            ),
            ImmutableMultimap.of(),
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_thoughness", 3, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_knockback", 25, EntityAttributeModifier.Operation.ADDITION)
            )
    );
    public static final Item LEGENDARY_SHIELD = new GuardianShield(
            10,
            -3.1f,
            ItemRarity.LEGENDARY,
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.PHYSICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_physical_resistance", 20, EntityAttributeModifier.Operation.ADDITION),
                    ModEntityAttributes.MAGICAL_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_magic_resistance", 20, EntityAttributeModifier.Operation.ADDITION)
            ),
            ImmutableMultimap.of(),
            ImmutableMultimap.of(
                    EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_armor", 20, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_thoughness", 3, EntityAttributeModifier.Operation.ADDITION),
                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(UUID.randomUUID(), "shield_active_knockback", 30, EntityAttributeModifier.Operation.ADDITION)
            )
    );

    public static final Item MYTHICAL_STAFF = new MagicWand(-3f, ImmutableMultimap.of(
            ModEntityAttributes.MAGIC_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "wand_magic_damage", 15, EntityAttributeModifier.Operation.ADDITION),
            ModEntityAttributes.MAGIC_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "wand_magic_damage_total_multiplier", 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
            ModEntityAttributes.HEAL_EFFICIENCY, new EntityAttributeModifier(UUID.randomUUID(), "wand_heal_add", 10, EntityAttributeModifier.Operation.ADDITION),
            ModEntityAttributes.HEAL_EFFICIENCY, new EntityAttributeModifier(UUID.randomUUID(), "wand_heal_total_multiplier", 2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
    ), ItemRarity.MYTHIC);

    public static final Item BLOOD_DAGGER = new Dagger(ToolMaterials.NETHERITE, 9, -1f);


    /// Trinkets

    public static final Item GLASSES = new Glasses();
    public static final Item PINK_RIBBON_TRINKET = new RibbonTrinket(Rarity.RARE, List.of(
            new StatusEffectInstance(StatusEffects.RESISTANCE, MaryMod2024.TICKS_PER_SECOND * 15, 0, false, true)
    ), ImmutableMultimap.of(
            EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(UUID.randomUUID(), "ribbon_max_hp", 4, EntityAttributeModifier.Operation.ADDITION)
    ));
    public static final Item GREEN_RIBBON_TRINKET = new RibbonTrinket(Rarity.RARE, List.of(
            new StatusEffectInstance(ModEffects.ZOMBIEFICATION, MaryMod2024.TICKS_PER_SECOND * 120)
    ), ImmutableMultimap.of(

    ));
    public static final Item BLACK_RIBBON_TRINKET = new RibbonTrinket(Rarity.RARE, List.of(
            new StatusEffectInstance(ModEffects.VAMPIRISM, MaryMod2024.TICKS_PER_SECOND * 120)
    ), ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(UUID.randomUUID(), "ribbon_atk_sp", 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
    ));
    public static final Item BLUE_RIBBON_TRINKET = new RibbonTrinket(Rarity.RARE, List.of(
    ), ImmutableMultimap.of(
    ));
    public static final Item CYAN_RIBBON_TRINKET = new RibbonTrinket(Rarity.RARE, List.of(

    ), ImmutableMultimap.of(

    ));
    public static final Item RED_RIBBON_TRINKET = new RibbonTrinket(Rarity.RARE, List.of(

    ), ImmutableMultimap.of(

    ));

    public static final Item MICROPHONE_TRINKET = new MicrophoneTrinket();

    public static final Item MANA_NECKLACE = new AccessoryTrinket(Rarity.RARE, ImmutableMultimap.of(
            ModEntityAttributes.MANA_REGENERATION, new EntityAttributeModifier(UUID.randomUUID(), "necklace_mana_regen", 0.5, EntityAttributeModifier.Operation.ADDITION),
            ModEntityAttributes.ADDITIONAL_MANA, new EntityAttributeModifier(UUID.randomUUID(), "necklace_add_mana", 10, EntityAttributeModifier.Operation.ADDITION)
    ));

    public static final Item ARCANE_RING = new AccessoryTrinket(Rarity.RARE, ImmutableMultimap.of(
            ModEntityAttributes.MANA_REGENERATION, new EntityAttributeModifier(UUID.randomUUID(), "necklace_mana_regen", 0.5, EntityAttributeModifier.Operation.ADDITION),
            ModEntityAttributes.ADDITIONAL_MANA, new EntityAttributeModifier(UUID.randomUUID(), "necklace_add_mana", 10, EntityAttributeModifier.Operation.ADDITION)
    ), 1);

    public static final Item MANA_RING = new AccessoryTrinket(Rarity.RARE, ImmutableMultimap.of(
            ModEntityAttributes.MANA_REGENERATION, new EntityAttributeModifier(UUID.randomUUID(), "necklace_mana_regen", 0.5, EntityAttributeModifier.Operation.ADDITION),
            ModEntityAttributes.ADDITIONAL_MANA, new EntityAttributeModifier(UUID.randomUUID(), "necklace_add_mana", 10, EntityAttributeModifier.Operation.ADDITION)
    ), 1);

    public static final Item MANA_BRACELET = new AccessoryTrinket(Rarity.RARE, ImmutableMultimap.of(
            ModEntityAttributes.MANA_REGENERATION, new EntityAttributeModifier(UUID.randomUUID(), "necklace_mana_regen", 1, EntityAttributeModifier.Operation.ADDITION),
            ModEntityAttributes.ADDITIONAL_MANA, new EntityAttributeModifier(UUID.randomUUID(), "necklace_add_mana", 50, EntityAttributeModifier.Operation.ADDITION)
    ));

    public static final Item GOLD_BRACELET = new AccessoryTrinket(Rarity.RARE, ImmutableMultimap.of(
            ModEntityAttributes.MAGIC_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "bracelet_magic_damage", 10, EntityAttributeModifier.Operation.ADDITION),
            ModEntityAttributes.DISTANCE_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "bracelet_distance_damage", 10, EntityAttributeModifier.Operation.ADDITION),
            EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "bracelet_damage", 10, EntityAttributeModifier.Operation.ADDITION)
    ));

    ///  SPELLS

    public static final Item REGEN_SPELL = SpellTrinketBuilder
            .start()
            .range(0)
            .handler((world, user, range, radius) -> {

            })
            .cooldown(5)
            .manaCost(100)
            .radius(10)
            .castTime(10)
            .build();

    public static final Item HOLY_LIGHT = SpellTrinketBuilder
            .start()
            .range(15)
            .radius(5)
            .cooldown(5)
            .manaCost(100)
            .castTime(10)
            .handler((world, user, range, radius) -> {
                final Vec3d originPos = SpellCastHelper.spellRayCast(world, user, range, 0f, false);

                Box area = new Box(
                        originPos.getX() - radius, originPos.getY() - radius, originPos.getZ() - radius,
                        originPos.getX() + radius, originPos.getY() + radius, originPos.getZ() + radius
                );

                final Predicate<LivingEntity> ENTITY_PREDICATE = entity -> entity.isAlive() && entity.isAttackable();

                final TargetPredicate TARGET_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(radius).setPredicate(ENTITY_PREDICATE);

                HolyLightSpellEntity.spawn((ServerWorld) world, originPos, user, () -> {
                    final var targets = world.getTargets(LivingEntity.class, TARGET_PREDICATE, null, area);
                    final int baseAmount = 200;

                    final var attributes = Objects.requireNonNull(user.getAttributes().getCustomInstance(ModEntityAttributes.HEAL_EFFICIENCY)).getModifiers();
                    final double sums = attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.ADDITION).mapToDouble(EntityAttributeModifier::getValue).sum() * 10;
                    final double bases = attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE).mapToDouble(EntityAttributeModifier::getValue).sum() + 1;
                    final double totals = attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL).mapToDouble(EntityAttributeModifier::getValue).sum() + 1;

                    final float totalAmount = (float) (((baseAmount * bases) + sums) * totals) * 10;
                    for (LivingEntity target : targets) {
                        if (target.isUndead()) {
                            target.damage(user.getDamageSources().create(DamageTypes.MAGIC, user), totalAmount);
                        } else {
                            target.heal(totalAmount);
                        }
                    }
                });

            })
            .build();


    public static final Item THUNDER_STORM = SpellTrinketBuilder
            .start()
            .range(20)
            .radius(3)
            .cooldown(15)
            .manaCost(150)
            .castTime(10)
            .handler((world, player, range, radius) -> {
                final Vec3d originPos = SpellCastHelper.spellRayCast(world, player, range, 0f, false);
                // cola de 3 rayos
                final int strikes = 3;              // cantidad de rayos
                final int spacingTicks = 25;         // separación entre rayos

                for (int i = 0; i < strikes; i++) {
                    int delay = i * spacingTicks + 4;
                    TickScheduler.schedule((ServerWorld) world, delay, () -> {
                        double ox = world.getRandom().nextBetween(-radius, radius);
                        double oz = world.getRandom().nextBetween(-radius, radius);

                        var targetPoint = originPos.add(ox, 0, oz);

                        SpellCastHelper.summonLightning(targetPoint, player, world);
                    });
                }
            })
            .build();

    public static final Item MANA_SHIELD = SpellTrinketBuilder.start()
            .range(0)
            .radius(0)
            .cooldown(15)
            .manaCost(80)
            .castTime(2)
            .handler((world, player, range, radius) -> {
                if (world.isClient) {
                    return;
                }

                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeUuid(player.getUuid());
                buffer.writeDouble(player.getX());
                buffer.writeDouble(player.getY());
                buffer.writeDouble(player.getZ());
                buffer.writeFloat(radius);
                buffer.writeLong(world.getTime() + 20 * 20);

                ServerPlayNetworking.send((ServerPlayerEntity) player, NetworkManager.CREATE_MANA_SHIELD_ID, buffer);

                // feedback audiovisual
                BlockPos pos = player.getBlockPos();
                world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, SoundCategory.PLAYERS, 1.0F, 0.9F);
                world.playSound(null, pos, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.9F, 0.9F);
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 0.6F, 0.6F);
            })
            .build();

    public static final Item TAUNT_SKILL = SpellTrinketBuilder
            .start()
            .castTime(10)
            .range(0)
            .radius(20)
            .cooldown(15)
            .manaCost(100)
            .handler((world, user, range, radius) -> {
                Box area = new Box(
                        user.getX() - radius, user.getY() - radius, user.getZ() - radius,
                        user.getX() + radius, user.getY() + radius, user.getZ() + radius
                );

                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1.2F, 0.8F);
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.9F);
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.6F, 1.0F);


                List<LivingEntity> enemies = user.getWorld().getEntitiesByClass(LivingEntity.class, area, e -> !e.isPlayer() && e.isAlive());
                if (enemies.isEmpty()) {
                    return;
                }

                user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 30, 3, false, true));

                for (var enemy : enemies) {
                    if (enemy instanceof MobEntity mob) {
                        mob.setTarget(user);
                        mob.getNavigation().startMovingTo(user, 1.0D);
                        mob.getBrain().remember(MemoryModuleType.ATTACK_TARGET, user, 200L); // 10 segundos aprox
                        ((ServerWorld) world).spawnParticles(
                                ParticleTypes.ANGRY_VILLAGER,
                                mob.getX(), mob.getY() + 1, mob.getZ(),
                                4, 0.4, 1, 0.4, 0.1
                        );
                    }
                }
            })
            .build();

    public static final Item HEALING_SPELL = SpellTrinketBuilder
            .start()
            .castTime(10)
            .range(0)
            .radius(5)
            .cooldown(5)
            .manaCost(100)
            .handler((world, user, range, radius) -> {
                final Predicate<LivingEntity> ENTITY_PREDICATE = entity -> entity.isPlayer() &&
                        entity.isAlive() &&
                        entity.isAttackable() &&
                        entity.getHealth() < entity.getMaxHealth();

                final TargetPredicate PLAYER_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(radius).setPredicate(ENTITY_PREDICATE);

                final int baseAmount = 50;

                final var attributes = Objects.requireNonNull(user.getAttributes().getCustomInstance(ModEntityAttributes.HEAL_EFFICIENCY)).getModifiers();
                final double sums = attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.ADDITION).mapToDouble(EntityAttributeModifier::getValue).sum() * 10;
                final double bases = attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE).mapToDouble(EntityAttributeModifier::getValue).sum() + 1;
                final double totals = attributes.stream().filter(i -> i.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL).mapToDouble(EntityAttributeModifier::getValue).sum() + 1;

                final float totalAmount = (float) (((baseAmount * bases) + sums) * totals);

                final var targets = world.getTargets(PlayerEntity.class, PLAYER_PREDICATE, null, user.getBoundingBox().expand(radius));
                for (LivingEntity target : targets) {
                    target.heal(totalAmount);
                }
            })
            .build();

    public static final Item MIDAS_SPELL = SpellTrinketBuilder
            .start()
            .castTime(10)
            .range(15)
            .radius(3)
            .cooldown(3)
            .manaCost(180)
            .handler((world, user, range, radius) -> {
                final Vec3d originPos = SpellCastHelper.spellRayCast(world, user, range, 0f, false);

                final int px = (int) originPos.getX();
                final int pz = (int) originPos.getZ();
                final int yLimit = (int) originPos.getY(); // “debajo del jugador”
                final int r2 = radius * radius;

                for (int dz = -radius; dz <= radius; dz++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        if (dx * dx + dz * dz > r2) continue; // círculo; quita esto si prefieres cuadrado

                        int x = px + dx;
                        int z = pz + dz;

                        // Top Y de bloque que bloquea movimiento (ignora hojas)
                        int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                        if (surfaceY < world.getBottomY()) continue;

                        // Limita a por debajo del jugador
                        if (surfaceY >= yLimit) {
                            surfaceY = yLimit - 1;
                        }
                        if (surfaceY < world.getBottomY()) continue;

                        BlockPos pos = new BlockPos(x, surfaceY, z);
                        BlockState state = world.getBlockState(pos);
                        if (!state.isAir()) {
                            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
                        }
                    }
                }
            })
            .build();

    public static final Item CURSED_SHADOW = SpellTrinketBuilder
            .start()
            .castTime(10)
            .range(15)
            .radius(4)
            .cooldown(15)
            .manaCost(250)
            .handler((world, user, range, radius) -> {
                final Vec3d originPos = SpellCastHelper.spellRayCast(world, user, range, 0f, false);

                final int px = (int) originPos.getX();
                final int pz = (int) originPos.getZ();
                final int yLimit = (int) originPos.getY(); // “debajo del jugador”


                AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world, px, yLimit, pz);
                areaEffectCloudEntity.setOwner(user);
                areaEffectCloudEntity.setRadius(radius);
                areaEffectCloudEntity.setWaitTime(5);
                areaEffectCloudEntity.setDuration(20 * 10);
                areaEffectCloudEntity.setPotion(Potions.HARMING);


                areaEffectCloudEntity.setColor(Colors.BLACK);

                world.spawnEntity(areaEffectCloudEntity);
            })
            .build();


    private static Item registerItem(final String name, final Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MaryMod2024.MOD_ID, name), item);
    }

    public static void registerModItems() {
        registerItem("mary_coin_item", MARY_COIN);

        registerItem("mary_gold", MARY_GOLD);
        registerItem("mary_silver", MARY_SILVER);
        registerItem("mary_copper", MARY_COPPER);

        registerItem("bandage", BANDAGE);
        registerItem("estus", ESTUS);

        registerItem("pink_ribbon", PINK_RIBBON);
        registerItem("pink_ribbon_trinket", PINK_RIBBON_TRINKET);
        registerItem("blue_ribbon_trinket", BLUE_RIBBON_TRINKET);
        registerItem("black_ribbon_trinket", BLACK_RIBBON_TRINKET);
        registerItem("cyan_ribbon_trinket", CYAN_RIBBON_TRINKET);
        registerItem("red_ribbon_trinket", RED_RIBBON_TRINKET);
        registerItem("green_ribbon_trinket", GREEN_RIBBON_TRINKET);

        registerItem("medika_potion", MEDIKA_POTION);
        registerItem("potion_case", POTION_CASE);
        registerItem("potion_case_2", POTION_CASE_2);

        registerItem("glasses", GLASSES);
        registerItem("tdah_pill", TDAH_PILL);
        registerItem("microphone_trinket", MICROPHONE_TRINKET);
        registerItem("mana_necklace", MANA_NECKLACE);

        registerItem("practice_boh_staff", PRACTICE_BOH_STAFF);
        registerItem("combat_boh_staff", COMBAT_BOH_STAFF);
        registerItem("armored_boh_staff", ARMORED_BOH_STAFF);
        registerItem("sublime_boh_staff", SUBLIME_BOH_STAFF);

        registerItem("mythical_staff", MYTHICAL_STAFF);

        registerItem("blood_dagger", BLOOD_DAGGER);

        registerItem("common_shield", COMMON_SHIELD);
        registerItem("uncommon_shield", UNCOMMON_SHIELD);
        registerItem("rare_shield", RARE_SHIELD);
        registerItem("epic_shield", EPIC_SHIELD);
        registerItem("legendary_shield", LEGENDARY_SHIELD);

        registerItem("healing_spell", HEALING_SPELL);
        registerItem("regen_spell", REGEN_SPELL);
        registerItem("holy_light", HOLY_LIGHT);
        registerItem("midas_spell", MIDAS_SPELL);
        registerItem("cursed_shadow", CURSED_SHADOW);
        registerItem("taunt_skill", TAUNT_SKILL);
        registerItem("mana_shield", MANA_SHIELD);
        registerItem("thunder_storm", THUNDER_STORM);

        registerItem("mana_ring", MANA_RING);
        registerItem("arcane_ring", ARCANE_RING);
        registerItem("mana_bracelet", MANA_BRACELET);
        registerItem("gold_bracelet", GOLD_BRACELET);

        ItemGroups.registerItemGroups();
    }
}