package com.paramada.marycum2024;

import com.paramada.marycum2024.attributes.ModComponents;
import com.paramada.marycum2024.attributes.ModEntityAttributes;
import com.paramada.marycum2024.attributes.mana.ManaComponent;
import com.paramada.marycum2024.attributes.shield.ShieldComponent;
import com.paramada.marycum2024.blocks.BlockManager;
import com.paramada.marycum2024.blocks.custom.entities.BlockEntityManager;
import com.paramada.marycum2024.effects.ModEffects;
import com.paramada.marycum2024.entities.ModEntities;
import com.paramada.marycum2024.entities.custom.BeagleEntity;
import com.paramada.marycum2024.events.AdvancementManager;
import com.paramada.marycum2024.events.SoundManager;
import com.paramada.marycum2024.items.ItemManager;
import com.paramada.marycum2024.items.custom.weapons.MagicWand;
import com.paramada.marycum2024.networking.NetworkManager;
import com.paramada.marycum2024.screens.handlers.ModScreenHandlers;
import com.paramada.marycum2024.spells.ManaShield;
import com.paramada.marycum2024.spells.registries.ManaShieldRegistry;
import com.paramada.marycum2024.util.functionality.TickScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MaryMod2024 implements ModInitializer {

    public static final String MOD_ID = "mary-mod-2024";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final int TICKS_PER_SECOND = 20;

    @Override
    public void onInitialize() {
        ModEntityAttributes.registerAttributes();
        ModScreenHandlers.registerScreenHandlersForServer();
        ModEffects.registerEffects();
        BlockEntityManager.registerEntities();
        BlockManager.registerModBlocks();
        ItemManager.registerModItems();
        AdvancementManager.registerCriterions();
        NetworkManager.registerC2SPackets();
        SoundManager.registerSounds();

        FabricDefaultAttributeRegistry.register(ModEntities.BEAGLE, BeagleEntity.createBeagleAttributes());
        TickScheduler.init();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                ManaComponent mana = ModComponents.MANA.get(p);
                ShieldComponent shield = ModComponents.SHIELD.get(p);
                mana.tick(p);
                shield.tick(p);
            }
        });
    }

    private static void spawnSphereShellParticles(ServerWorld world, Vec3d c, int r) {
        // muestreo lat-long: 18 x 36 = 648 puntos aprox
        int latSteps = 18, lonSteps = 36;
        for (int i = 0; i < latSteps; i++) {
            double theta = Math.PI * (i + 0.5) / latSteps;      // 0..π
            double st = Math.sin(theta), ct = Math.cos(theta);
            for (int j = 0; j < lonSteps; j += 2) {             // saltar de 2 en 2 = menos puntos
                double phi = 2 * Math.PI * j / lonSteps;        // 0..2π
                double x = r * st * Math.cos(phi);
                double y = r * ct;
                double z = r * st * Math.sin(phi);
                world.spawnParticles(ParticleTypes.END_ROD, c.x + x, c.y + y, c.z + z, 1, 0, 0, 0, 0);
            }
        }
    }

}
/*
    lanza
    espadon
    espada

 */


/*
TODO: CREDITS
modelos:
themodderG
jojoshua
ferrum_26
para_mada

arte:
alavialam
themodderG
para_mada
chema_ftw

programacion:
para_mada

historia:
bolt

construccion:
monalisa2015
kodcake
el compa de monalisa
para_mada

npcs:
conterstine maid
ansichan
vickypalami

 */