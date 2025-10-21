package com.paramada.marycum2024.entities.custom;

import com.paramada.marycum2024.entities.ModEntities;
import com.paramada.marycum2024.entities.ai.BeagleAttackGoal;
import com.paramada.marycum2024.entities.ai.BeagleAutoFindEnemiesGoal;
import com.paramada.marycum2024.entities.ai.BeagleEscapeDangerGoal;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class BeagleEntity extends WolfEntity {
    public static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(BeagleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> BARKING = DataTracker.registerData(BeagleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState barkAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private int barkAnimationTimeout = 0;
    public int biteAnimationTimeout = 0;

    public BeagleEntity(EntityType<? extends BeagleEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public boolean isBarking() {
        return this.dataTracker.get(BARKING);
    }

    public void setBarking(boolean barking) {
        this.dataTracker.set(BARKING, barking);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(BARKING, false);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (isAttacking() && this.biteAnimationTimeout <= 0) {
            this.biteAnimationTimeout = 40;
            this.biteAnimationState.start(this.age);
        } else {
            --this.biteAnimationTimeout;
        }
        if (!isAttacking()) {
            biteAnimationState.stop();
        }

        if (isBarking() && this.barkAnimationTimeout <= 0) {
            this.barkAnimationTimeout = 40;
            this.barkAnimationState.start(this.age);
        } else {
            --this.barkAnimationTimeout;
        }
        if (!isBarking()) {
            barkAnimationState.stop();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            setupAnimationStates();
        } else if (!this.isTamed()){
            var player = getEntityWorld().getClosestPlayer(TargetPredicate.DEFAULT.setBaseMaxDistance(10), this);
            if (player != null) {
                this.setOwner(player);
            }
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new BeagleEscapeDangerGoal(this, 1.5));
        this.goalSelector.add(3, new BeagleAutoFindEnemiesGoal(this));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));

        this.goalSelector.add(5, new BeagleAttackGoal(this, 1, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1d));
        this.goalSelector.add(9, new WolfBegGoal(this, 8.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this)).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
    }

    public static DefaultAttributeContainer.Builder createBeagleAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1024)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2)
                .add(EntityAttributes.GENERIC_ARMOR, 30);
    }

    @Override
    public BeagleEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.BEAGLE.create(world);
    }
}
