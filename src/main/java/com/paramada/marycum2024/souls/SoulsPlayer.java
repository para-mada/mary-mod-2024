package com.paramada.marycum2024.souls;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.paramada.marycum2024.items.trinkets.bases.SpellTrinket;
import com.paramada.marycum2024.math.Functions;
import com.paramada.marycum2024.math.Rect;
import com.paramada.marycum2024.networking.NetworkManager;
import com.paramada.marycum2024.util.functionality.bridges.LivingEntityBridge;
import com.paramada.marycum2024.util.inventory.SpecialSlotManager;
import com.paramada.marycum2024.util.souls.ISoulsPlayerCamera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Predicate;

public class SoulsPlayer implements ISoulsPlayerCamera {

    private static final Predicate<LivingEntity> ENTITY_PREDICATE = entity -> entity.isAlive() && entity.isAttackable() && !entity.isPlayer();
    public final PlayerEntity player;
    private final MinecraftClient client;

    public final SpecialSlotManager itemSelectorManager = new SpecialSlotManager(23, 27);
    public final SpecialSlotManager offHandSelectorManager = new SpecialSlotManager(10, 14);

    private LivingEntity lockedTarget = null;
    private int selectedSpellSlot = 0;

    private boolean enabledPrimarySwap = true;
    private boolean enabledSecondarySwap = true;
    private boolean enabledHeavyAttack = true;
    private boolean enabledAttack = true;

    private boolean swappedItem = false;
    private boolean enabledReturnItemToPosition = false;

    private SoulsAction currentAction = SoulsAction.IDLE;

    public SoulsPlayer(PlayerEntity player) {
        this.player = player;
        client = MinecraftClient.getInstance();
    }

    public SoulsPlayer(LivingEntity player) {
        this((PlayerEntity) player);
    }

    public void performHeavyAttack() {
        if (!enabledHeavyAttack) return;

        if (!currentAction.allows(SoulsAction.ATTACKING_HEAVY)) {
            return;
        }

        currentAction = SoulsAction.ATTACKING_HEAVY;

    }

    public void performLightAttack() {
        if (!enabledAttack) return;

        if (!currentAction.allows(SoulsAction.ATTACKING)) {
            return;
        }

        currentAction = SoulsAction.ATTACKING;

    }

    public void enableHeavyAttack() {
        if (!enabledHeavyAttack) {
            enabledHeavyAttack = true;
        }
    }

    public void enableLightAttack() {
        if (!enabledAttack) {
            enabledAttack = true;
        }
    }

    @Override
    public LivingEntity getLockedTarget() {
        return lockedTarget;
    }

    @Override
    public boolean hasLockedTarget() {
        return lockedTarget != null;
    }

    @Override
    public void setLockedTarget(Entity target) {
        if (target == null) {
            lockedTarget = null;
        } else if (target instanceof LivingEntity living) {
            lockedTarget = living;
        }
    }

    public void setCurrentSpellSlot(int spell) {
        selectedSpellSlot = spell;
    }

    public void tick() {
        var shoulderSurfing = ShoulderSurfing.getInstance();
        var tickDelta = client.getTickDelta();

        if (shoulderSurfing != null) {
            if (!player.isSpectator() && !player.isCreative() && !shoulderSurfing.isShoulderSurfing()) {
                //shoulderSurfing.changePerspective(Perspective.SHOULDER_SURFING);
            }
            if (this.hasLockedTarget() && player.getWorld().isClient && !client.isPaused()) {
                var camera = shoulderSurfing.getCamera();
                var target = lockedTarget;
                var direction = target.getLerpedPos(tickDelta).subtract(player.getEyePos()).normalize();
                var newYaw = getNewDeltaYaw(camera.getYRot(), direction);
                if (Math.abs(newYaw - camera.getYRot()) > 0.01f) {
                    player.setYaw(newYaw);
                    camera.setYRot(newYaw);
                }
            }
        } else {
            if (this.hasLockedTarget() && player.getWorld().isClient && !client.isPaused()) {
                var target = lockedTarget;
                var direction = target.getLerpedPos(tickDelta).subtract(player.getEyePos()).normalize();
                var yawDelta = getNewDeltaYaw(player.getYaw(), direction);
                player.setYaw(yawDelta);
            }
        }

        if (lockedTarget != null && lockedTarget.isDead()) {
            lockedTarget = null;
        }
    }

    private float getNewDeltaYaw(final float playerYaw, Vec3d directionVec) {
        final float tickDelta = client.getTickDelta();
        final float degree = (float) Functions.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        return Functions.lerpAngleYaw(playerYaw, degree, 1);
    }

    public void switchPrimaryHand() {
        if (!currentAction.allows(SoulsAction.CHANGE_ITEM)) {
            return;
        }

        if (enabledPrimarySwap) {
            var selectedSlot = player.getInventory().selectedSlot;
            player.getInventory().selectedSlot = getNextNonEmptySlot(selectedSlot);
            syncSelectedSlot();
            enabledPrimarySwap = false;
        }
    }

    private int getNextNonEmptySlot(int selectedSlot) {
        for (int i = selectedSlot + 1; i < 9; i++) {
            var stack = player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                return i;
            }
        }
        for (int i = 0; i < selectedSlot; i++) {
            var stack = player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                return i;
            }
        }
        return selectedSlot;
    }

    public void returnSelectedItem() {
        if (currentAction != SoulsAction.USING_ITEM) {
            return;
        }

        if (!enabledReturnItemToPosition) {
            return;
        }

        currentAction = SoulsAction.IDLE;

        client.options.useKey.setPressed(false);

        enabledReturnItemToPosition = false;
        swappedItem = false;

        var currentItem = itemSelectorManager.getSelectedSlot();
        NetworkManager.swapHandWithSelectedItem(currentItem, false);
    }

    public void startUsingItem() {
        if (enabledReturnItemToPosition) {
            return;
        }

        if (!currentAction.allows(SoulsAction.USING_ITEM)) {
            return;
        }

        currentAction = SoulsAction.USING_ITEM;

        swappedItem = true;
        enabledReturnItemToPosition = true;

        var currentItem = itemSelectorManager.getSelectedSlot();

        NetworkManager.swapHandWithSelectedItem(currentItem, true);
    }

    public void lockFocusedTarget() {
        var targets = getPossibleTargets();

        LivingEntity target = getClossestTarget(targets);

        if (this.hasLockedTarget()) {
            this.setLockedTarget(null);
        } else if (target != null) {
            this.setLockedTarget(target);
        }
    }

    private List<LivingEntity> getPossibleTargets() {
        var shoulderSurfing = (ShoulderSurfingImpl) ShoulderSurfing.getInstance();
        var camera = shoulderSurfing.getCamera();

        if (camera != null) {
            var maxDistance = 10d;

            final TargetPredicate ENEMY_CONDITION = TargetPredicate.createAttackable().setBaseMaxDistance(maxDistance).setPredicate(ENTITY_PREDICATE);


            return player.getWorld()
                    .getTargets(LivingEntity.class, ENEMY_CONDITION, player, player.getBoundingBox().expand(maxDistance)).stream().filter(player::canSee).toList();

        }
        return List.of();
    }

    private LivingEntity getClossestTarget(List<LivingEntity> targets) {
        LivingEntity result = targets.isEmpty() ? null : targets.get(0);
        var shoulderSurfing = (ShoulderSurfingImpl) ShoulderSurfing.getInstance();
        var camera = shoulderSurfing.getCamera();
        if (camera != null && player != null) {
            var pitch = camera.getXRot(); // vertical facing
            var yaw = camera.getYRot(); // horizontal facing
            var eyeCenter = player.getPos();
            var lookingAt = new Rect(eyeCenter, yaw, pitch);

            for (LivingEntity target : targets) {
                if (result == null || target.squaredDistanceTo(player) < result.squaredDistanceTo(player) && lookingAt.distanceToVector(target.getPos()) < lookingAt.distanceToVector(result.getPos())) {
                    result = target;
                }
            }
        }

        return result;
    }

    private void syncSelectedSlot() {
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }

        int i = player.getInventory().selectedSlot;
        client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));
    }

    public void selectNextItem() {
        if (!currentAction.allows(SoulsAction.CHANGE_ITEM)) {
            return;
        }

        itemSelectorManager.selectNextSlot();
    }

    public void switchSecondaryHand() {
        if (!currentAction.allows(SoulsAction.CHANGE_ITEM)) {
            return;
        }

        // TODO
    }

    public void enableSwitchPrimary() {
        if (!enabledPrimarySwap) {
            enabledPrimarySwap = true;
        }
    }

    public void enableSwitchSecondary() {
        if (!enabledSecondarySwap) {
            enabledSecondarySwap = true;
        }
    }

    public SoulsAction getCurrentAction() {
        return currentAction;
    }

    public boolean isSwappedItem() {
        return swappedItem;
    }

    public void stopUsingItem() {
        currentAction = SoulsAction.IDLE;
    }

    public SpellTrinket getCurrentSpell() {
        var trinketComponent = LivingEntityBridge.getTrinketComponent(player);
        var spells = trinketComponent.getEquipped(stack -> stack.getItem() instanceof SpellTrinket);
        var filter = spells.stream().filter(data -> data.getLeft().index() == selectedSpellSlot).findFirst();
        SpellTrinket spell = null;
        if (filter.isPresent()) {
            spell = (SpellTrinket) filter.get().getRight().getItem();
        }
        return spell;
    }

    public int getCurrentSpellSlot() {
        return selectedSpellSlot;
    }

    public enum SoulsAction {
        ATTACKING,
        ATTACKING_HEAVY,
        CHANGE_ITEM(true),
        USING_ITEM(true),
        IDLE(true);

        public final boolean allowsActions;
        private final List<SoulsAction> allowedActions;

        SoulsAction(boolean allowsActions) {
            this(allowsActions, List.of());
        }

        SoulsAction() {
            this(false, List.of());
        }

        SoulsAction(boolean allowsActions, List<SoulsAction> actions) {
            this.allowsActions = allowsActions;
            this.allowedActions = actions;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean allows(SoulsAction action) {

            if (!allowsActions) {
                return action == IDLE;
            }

            if (allowedActions.isEmpty()) {
                return true;
            }

            return this.allowedActions.contains(action);
        }
    }
}
