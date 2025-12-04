package com.lirxowo.evilseagull.mixin;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.lirxowo.evilseagull.advancement.ESAdvancementTriggerRegistry;
import com.lirxowo.evilseagull.compat.AppliedEnergisticsCompat;
import com.lirxowo.evilseagull.compat.CreateCompat;
import com.lirxowo.evilseagull.compat.RefinedStorageCompat;
import com.lirxowo.evilseagull.compat.SophisticatedBackpacksCompat;
import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SeagullAIStealFromPlayers.class)
public abstract class SeagullAIStealFromPlayersMixin extends Goal {

    @Shadow(remap = false)
    @Final
    private EntitySeagull seagull;

    @Shadow(remap = false)
    private Vec3 fleeVec;

    @Shadow(remap = false)
    private int fleeTime;

    @Unique
    private BlockPos evilSeagull$targetMEInterface = null;

    @Unique
    private boolean evilSeagull$stealingFromME = false;

    @Unique
    private BlockPos evilSeagull$targetBackpackBlock = null;

    @Unique
    private boolean evilSeagull$stealingFromBackpackBlock = false;

    @Unique
    private BlockPos evilSeagull$targetRSInterface = null;

    @Unique
    private boolean evilSeagull$stealingFromRS = false;

    @Unique
    private BlockPos evilSeagull$targetBeltController = null;

    @Unique
    private Vec3 evilSeagull$targetItemWorldPos = null;

    @Unique
    private int evilSeagull$targetItemIndex = -1;

    @Unique
    private boolean evilSeagull$stealingFromBelt = false;

    @Unique
    private Vec3 evilSeagull$dropLocation = null;

    @Unique
    private boolean evilSeagull$flyingToDropLocation = false;

    @Unique
    private boolean evilSeagull$isHovering = false;

    @Unique
    private int evilSeagull$hoverTime = 0;

    @Unique
    private int evilSeagull$hoverDuration = 0;

    @Unique
    private Vec3 evilSeagull$hoverPosition = null;

    @Unique
    private boolean evilSeagull$shouldDropItem = false;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void evilSeagull$onCanUse(CallbackInfoReturnable<Boolean> cir) {
        evilSeagull$resetAllStates();
    }

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$afterCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && seagull.getMainHandItem().isEmpty()) {
            if (!AMConfig.seagullStealing || seagull.stealCooldown > 0 || seagull.isSitting()) {
                return;
            }

            if (SophisticatedBackpacksCompat.isModLoaded() && EvilSeagullConfig.STEAL_FROM_PLACED_BACKPACKS.get()) {
                List<SophisticatedBackpacksCompat.BackpackBlockInfo> backpacks = SophisticatedBackpacksCompat.findNearbyBackpackBlocksWithItems(seagull.level(), seagull.blockPosition(), this::evilSeagull$isBlacklisted);

                if (!backpacks.isEmpty()) {
                    SophisticatedBackpacksCompat.BackpackBlockInfo closest = evilSeagull$findClosestBackpack(backpacks);
                    if (closest != null) {
                        evilSeagull$targetBackpackBlock = closest.pos();
                        evilSeagull$stealingFromBackpackBlock = true;
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }

            if (AppliedEnergisticsCompat.isModLoaded() && EvilSeagullConfig.STEAL_FROM_ME_INTERFACE.get()) {
                List<AppliedEnergisticsCompat.MEInterfaceInfo> interfaces = AppliedEnergisticsCompat.findNearbyMEInterfacesWithItems(seagull.level(), seagull.blockPosition(), this::evilSeagull$isBlacklisted);

                if (!interfaces.isEmpty()) {
                    AppliedEnergisticsCompat.MEInterfaceInfo closest = evilSeagull$findClosestMEInterface(interfaces);
                    if (closest != null) {
                        evilSeagull$targetMEInterface = closest.pos;
                        evilSeagull$stealingFromME = true;
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }

            if (RefinedStorageCompat.isModLoaded() && EvilSeagullConfig.STEAL_FROM_RS_INTERFACE.get()) {
                List<RefinedStorageCompat.RSInterfaceInfo> interfaces = RefinedStorageCompat.findNearbyRSInterfacesWithItems(seagull.level(), seagull.blockPosition(), this::evilSeagull$isBlacklisted);

                if (!interfaces.isEmpty()) {
                    RefinedStorageCompat.RSInterfaceInfo closest = evilSeagull$findClosestRSInterface(interfaces);
                    if (closest != null) {
                        evilSeagull$targetRSInterface = closest.pos;
                        evilSeagull$stealingFromRS = true;
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }

            if (CreateCompat.isModLoaded() && EvilSeagullConfig.STEAL_FROM_CREATE_BELT.get()) {
                List<CreateCompat.BeltItemInfo> beltItems = CreateCompat.findNearbyBeltsWithItems(seagull.level(), seagull.blockPosition(), this::evilSeagull$isBeltItemBlacklisted);

                if (!beltItems.isEmpty()) {
                    CreateCompat.BeltItemInfo closest = evilSeagull$findClosestBeltItem(beltItems);
                    if (closest != null) {
                        evilSeagull$targetBeltController = closest.beltControllerPos;
                        evilSeagull$targetItemWorldPos = closest.itemWorldPos;
                        evilSeagull$targetItemIndex = closest.itemIndex;
                        evilSeagull$stealingFromBelt = true;
                        evilSeagull$initializeHovering();
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Inject(method = "hasFoods", at = @At("RETURN"), cancellable = true, remap = false)
    private void evilSeagull$onHasFoods(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }

        if (SophisticatedBackpacksCompat.hasItemsInBackpacks(player, this::evilSeagull$isBlacklisted)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getFoodItemFrom", at = @At("RETURN"), cancellable = true, remap = false)
    private void evilSeagull$onGetFoodItemFrom(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty()) {
            if (EvilSeagullConfig.PRIORITIZE_PLAYER_INVENTORY.get()) {
                return;
            }
        }

        ItemStack backpackItem = SophisticatedBackpacksCompat.extractItemFromBackpacks(player, this::evilSeagull$isBlacklisted);
        if (!backpackItem.isEmpty()) {
            if (cir.getReturnValue().isEmpty()) {
                seagull.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.PLAYERS, 1.0F, 1.0F);

                if (backpackItem.is(Items.BAKED_POTATO)) {
                    evilSeagull$triggerAdvancementForNearbyPlayers(player.blockPosition());
                }

                cir.setReturnValue(backpackItem);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void evilSeagull$onTick(CallbackInfo ci) {
        if (evilSeagull$flyingToDropLocation && evilSeagull$dropLocation != null) {
            evilSeagull$handleDropLogic();
            ci.cancel();
            return;
        }

        if (evilSeagull$stealingFromBackpackBlock && evilSeagull$targetBackpackBlock != null) {
            evilSeagull$handleBackpackBlockStealing();
            ci.cancel();
            return;
        }

        if (evilSeagull$stealingFromME && evilSeagull$targetMEInterface != null) {
            evilSeagull$handleMEStealing();
            ci.cancel();
            return;
        }

        if (evilSeagull$stealingFromRS && evilSeagull$targetRSInterface != null) {
            evilSeagull$handleRSStealing();
            ci.cancel();
            return;
        }

        if (evilSeagull$stealingFromBelt && evilSeagull$targetBeltController != null) {
            evilSeagull$handleBeltStealing();
            ci.cancel();
        }
    }

    @Inject(method = "canContinueToUse", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$onCanContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        if (evilSeagull$flyingToDropLocation) {
            cir.setReturnValue(evilSeagull$dropLocation != null);
            return;
        }
        if (evilSeagull$stealingFromBackpackBlock) {
            boolean canContinue = evilSeagull$targetBackpackBlock != null && (seagull.getMainHandItem().isEmpty() || fleeTime > 0 || evilSeagull$shouldDropItem);
            cir.setReturnValue(canContinue);
            return;
        }
        if (evilSeagull$stealingFromME) {
            boolean canContinue = evilSeagull$targetMEInterface != null && (seagull.getMainHandItem().isEmpty() || fleeTime > 0 || evilSeagull$shouldDropItem);
            cir.setReturnValue(canContinue);
            return;
        }
        if (evilSeagull$stealingFromRS) {
            boolean canContinue = evilSeagull$targetRSInterface != null && (seagull.getMainHandItem().isEmpty() || fleeTime > 0 || evilSeagull$shouldDropItem);
            cir.setReturnValue(canContinue);
            return;
        }
        if (evilSeagull$stealingFromBelt) {
            boolean canContinue = evilSeagull$targetBeltController != null &&
                (evilSeagull$isHovering || !seagull.getMainHandItem().isEmpty() || evilSeagull$flyingToDropLocation || evilSeagull$targetItemWorldPos != null);
            cir.setReturnValue(canContinue);
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void evilSeagull$onStop(CallbackInfo ci) {
        evilSeagull$resetAllStates();
    }

    @Unique
    private void evilSeagull$resetAllStates() {
        evilSeagull$targetMEInterface = null;
        evilSeagull$stealingFromME = false;
        evilSeagull$targetBackpackBlock = null;
        evilSeagull$stealingFromBackpackBlock = false;
        evilSeagull$targetRSInterface = null;
        evilSeagull$stealingFromRS = false;
        evilSeagull$targetBeltController = null;
        evilSeagull$targetItemWorldPos = null;
        evilSeagull$targetItemIndex = -1;
        evilSeagull$stealingFromBelt = false;
        evilSeagull$dropLocation = null;
        evilSeagull$flyingToDropLocation = false;
        evilSeagull$isHovering = false;
        evilSeagull$hoverTime = 0;
        evilSeagull$hoverDuration = 0;
        evilSeagull$hoverPosition = null;
        evilSeagull$shouldDropItem = false;
    }

    @Unique
    private boolean evilSeagull$isBlacklisted(ItemStack stack) {
        ResourceLocation loc = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (loc != null) {
            for (String str : AMConfig.seagullStealingBlacklist) {
                if (loc.toString().equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private void evilSeagull$triggerAdvancementForNearbyPlayers(BlockPos pos) {
        if (!seagull.level().isClientSide) {
            AABB searchBox = new AABB(pos).inflate(16.0);
            List<ServerPlayer> nearbyPlayers = seagull.level().getEntitiesOfClass(ServerPlayer.class, searchBox);

            for (ServerPlayer player : nearbyPlayers) {
                ESAdvancementTriggerRegistry.SEAGULL_STEAL_BAKED_POTATO.trigger(player);
            }
        }
    }

    @Unique
    private void evilSeagull$handleStolenItem(ItemStack stolenItem, BlockPos sourcePos) {
        seagull.peck();
        seagull.setItemInHand(InteractionHand.MAIN_HAND, stolenItem);

        seagull.level().playSound(null, sourcePos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stolenItem.is(Items.BAKED_POTATO)) {
            evilSeagull$triggerAdvancementForNearbyPlayers(sourcePos);
        }

        int baseCooldown = 1500 + seagull.getRandom().nextInt(1500);
        int modifier = EvilSeagullConfig.STEAL_COOLDOWN_MODIFIER.get();
        seagull.stealCooldown = baseCooldown * modifier / 100;

        if (stolenItem.isEdible()) {
            fleeTime = 60;
            evilSeagull$shouldDropItem = false;
        } else {
            evilSeagull$shouldDropItem = true;
            evilSeagull$dropLocation = evilSeagull$findRandomDropLocation();
            evilSeagull$flyingToDropLocation = true;
        }
    }

    @Unique
    private void evilSeagull$handleFleeOrDropLogic() {
        if (evilSeagull$shouldDropItem && evilSeagull$dropLocation != null) {
            evilSeagull$handleDropLogic();
        } else if (fleeTime > 0) {
            if (fleeVec == null) {
                fleeVec = seagull.getBlockInViewAway(seagull.position(), 4);
            }
            if (fleeVec != null) {
                seagull.setFlying(true);
                seagull.getMoveControl().setWantedPosition(fleeVec.x, fleeVec.y, fleeVec.z, 1.2F);

                if (seagull.distanceToSqr(fleeVec) < 5) {
                    fleeVec = seagull.getBlockInViewAway(fleeVec, 4);
                }
            }
            fleeTime--;
        }
    }

    @Unique
    private void evilSeagull$moveToTarget(Vec3 targetVec) {
        seagull.setFlying(true);
        Vec3 adjustedTarget = targetVec.add(0, 1, 0);
        seagull.getMoveControl().setWantedPosition(adjustedTarget.x, adjustedTarget.y, adjustedTarget.z, 1.2F);
    }

    @Unique
    private boolean evilSeagull$isBeltItemBlacklisted(ItemStack stack) {
        if (!EvilSeagullConfig.CREATE_BELT_STEAL_ANY_ITEM.get()) {
            if (!stack.isEdible()) {
                return true;
            }
        }
        return evilSeagull$isBlacklisted(stack);
    }

    @Unique
    private void evilSeagull$initializeHovering() {
        evilSeagull$isHovering = true;
        evilSeagull$hoverTime = 0;
        int minHover = EvilSeagullConfig.CREATE_BELT_HOVER_TIME_MIN.get();
        int maxHover = EvilSeagullConfig.CREATE_BELT_HOVER_TIME_MAX.get();
        evilSeagull$hoverDuration = minHover + seagull.getRandom().nextInt(Math.max(1, maxHover - minHover + 1));

        double hoverHeight = 3 + seagull.getRandom().nextDouble() * 2;
        evilSeagull$hoverPosition = new Vec3(
            evilSeagull$targetItemWorldPos.x,
            evilSeagull$targetItemWorldPos.y + hoverHeight,
            evilSeagull$targetItemWorldPos.z
        );
    }

    @Unique
    private void evilSeagull$handleHoveringPhase() {
        seagull.setFlying(true);

        if (evilSeagull$hoverPosition != null) {
            double time = seagull.tickCount * 0.05;
            double radius = 1.5;
            double offsetX = Math.cos(time) * radius;
            double offsetZ = Math.sin(time) * radius;

            Vec3 circlePos = new Vec3(
                evilSeagull$hoverPosition.x + offsetX,
                evilSeagull$hoverPosition.y,
                evilSeagull$hoverPosition.z + offsetZ
            );

            seagull.getMoveControl().setWantedPosition(circlePos.x, circlePos.y, circlePos.z, 1.0F);
        }

        evilSeagull$hoverTime++;

        if (evilSeagull$hoverTime >= evilSeagull$hoverDuration) {
            evilSeagull$isHovering = false;
            evilSeagull$hoverPosition = null;

            List<CreateCompat.BeltItemInfo> beltItems = CreateCompat.findNearbyBeltsWithItems(
                seagull.level(),
                seagull.blockPosition(),
                this::evilSeagull$isBeltItemBlacklisted
            );

            if (!beltItems.isEmpty()) {
                CreateCompat.BeltItemInfo closest = evilSeagull$findClosestBeltItem(beltItems);
                if (closest != null) {
                    evilSeagull$targetBeltController = closest.beltControllerPos;
                    evilSeagull$targetItemWorldPos = closest.itemWorldPos;
                    evilSeagull$targetItemIndex = closest.itemIndex;
                }
            } else {
                evilSeagull$stealingFromBelt = false;
                evilSeagull$targetBeltController = null;
                evilSeagull$targetItemWorldPos = null;
                evilSeagull$targetItemIndex = -1;
            }
        }
    }

    @Unique
    private void evilSeagull$handleStolenBeltItem(ItemStack stolenItem, BlockPos sourcePos) {
        seagull.peck();
        seagull.setItemInHand(InteractionHand.MAIN_HAND, stolenItem);

        seagull.level().playSound(null, sourcePos, SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);

        evilSeagull$targetItemWorldPos = null;
        evilSeagull$targetItemIndex = -1;

        if (stolenItem.is(Items.BAKED_POTATO)) {
            evilSeagull$triggerAdvancementForNearbyPlayers(sourcePos);
        }

        int baseCooldown = 1500 + seagull.getRandom().nextInt(1500);
        int modifier = EvilSeagullConfig.STEAL_COOLDOWN_MODIFIER.get();
        seagull.stealCooldown = baseCooldown * modifier / 100;

        if (stolenItem.isEdible()) {
            evilSeagull$stealingFromBelt = false;
            evilSeagull$targetBeltController = null;
            fleeTime = 60;
        } else {
            evilSeagull$dropLocation = evilSeagull$findRandomDropLocation();
            evilSeagull$flyingToDropLocation = true;
        }
    }

    @Unique
    private Vec3 evilSeagull$findRandomDropLocation() {
        int minRange = EvilSeagullConfig.DROP_RANGE_MIN.get();
        int maxRange = EvilSeagullConfig.DROP_RANGE_MAX.get();

        int distance = minRange + seagull.getRandom().nextInt(Math.max(1, maxRange - minRange + 1));
        double angle = seagull.getRandom().nextDouble() * Math.PI * 2;

        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;

        Vec3 currentPos = seagull.position();
        double targetX = currentPos.x + offsetX;
        double targetZ = currentPos.z + offsetZ;

        BlockPos targetBlockPos = BlockPos.containing(targetX, currentPos.y, targetZ);

        for (int y = (int) currentPos.y + 10; y > seagull.level().getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(targetBlockPos.getX(), y, targetBlockPos.getZ());
            if (!seagull.level().getBlockState(checkPos).isAir() &&
                seagull.level().getBlockState(checkPos.above()).isAir()) {
                return new Vec3(targetX, y + 2, targetZ);
            }
        }

        return new Vec3(targetX, currentPos.y, targetZ);
    }

    @Unique
    private void evilSeagull$handleDropLogic() {
        if (evilSeagull$dropLocation == null) {
            evilSeagull$flyingToDropLocation = false;
            return;
        }

        seagull.setFlying(true);
        seagull.getMoveControl().setWantedPosition(evilSeagull$dropLocation.x, evilSeagull$dropLocation.y, evilSeagull$dropLocation.z, 1.2F);

        double distToTarget = seagull.position().distanceTo(evilSeagull$dropLocation);
        if (distToTarget < 4.0) {
            ItemStack heldItem = seagull.getMainHandItem();
            if (!heldItem.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(
                    seagull.level(),
                    seagull.getX(),
                    seagull.getY(),
                    seagull.getZ(),
                    heldItem.copy()
                );

                double velocityX = (seagull.getRandom().nextDouble() - 0.5) * 0.2;
                double velocityY = seagull.getRandom().nextDouble() * 0.1;
                double velocityZ = (seagull.getRandom().nextDouble() - 0.5) * 0.2;
                itemEntity.setDeltaMovement(velocityX, velocityY, velocityZ);

                seagull.level().addFreshEntity(itemEntity);

                seagull.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

                seagull.level().playSound(null, seagull.getX(), seagull.getY(), seagull.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.5F, 0.8F);
            }

            evilSeagull$resetAllStates();
        }
    }

    @Unique
    private void evilSeagull$handleBackpackBlockStealing() {
        Vec3 targetVec = Vec3.atCenterOf(evilSeagull$targetBackpackBlock);
        evilSeagull$moveToTarget(targetVec);

        if (seagull.distanceToSqr(targetVec) < 4.0 && seagull.getMainHandItem().isEmpty()) {
            ItemStack stolenItem = SophisticatedBackpacksCompat.extractItemFromBackpackBlock(seagull.level(), evilSeagull$targetBackpackBlock, this::evilSeagull$isBlacklisted);

            if (!stolenItem.isEmpty()) {
                evilSeagull$handleStolenItem(stolenItem, evilSeagull$targetBackpackBlock);
            } else {
                evilSeagull$stealingFromBackpackBlock = false;
                evilSeagull$targetBackpackBlock = null;
            }
        }

        evilSeagull$handleFleeOrDropLogic();
    }

    @Unique
    private void evilSeagull$handleMEStealing() {
        Vec3 targetVec = Vec3.atCenterOf(evilSeagull$targetMEInterface);
        evilSeagull$moveToTarget(targetVec);

        if (seagull.distanceToSqr(targetVec) < 4.0 && seagull.getMainHandItem().isEmpty()) {
            ItemStack stolenItem = AppliedEnergisticsCompat.extractItemFromMEInterface(seagull.level(), evilSeagull$targetMEInterface, this::evilSeagull$isBlacklisted);

            if (!stolenItem.isEmpty()) {
                evilSeagull$handleStolenItem(stolenItem, evilSeagull$targetMEInterface);
            } else {
                evilSeagull$stealingFromME = false;
                evilSeagull$targetMEInterface = null;
            }
        }

        evilSeagull$handleFleeOrDropLogic();
    }

    @Unique
    private void evilSeagull$handleRSStealing() {
        Vec3 targetVec = Vec3.atCenterOf(evilSeagull$targetRSInterface);
        evilSeagull$moveToTarget(targetVec);

        if (seagull.distanceToSqr(targetVec) < 4.0 && seagull.getMainHandItem().isEmpty()) {
            ItemStack stolenItem = RefinedStorageCompat.extractItemFromRSInterface(seagull.level(), evilSeagull$targetRSInterface, this::evilSeagull$isBlacklisted);

            if (!stolenItem.isEmpty()) {
                evilSeagull$handleStolenItem(stolenItem, evilSeagull$targetRSInterface);
            } else {
                evilSeagull$stealingFromRS = false;
                evilSeagull$targetRSInterface = null;
            }
        }

        evilSeagull$handleFleeOrDropLogic();
    }

    @Unique
    private void evilSeagull$handleBeltStealing() {
        if (evilSeagull$flyingToDropLocation && evilSeagull$dropLocation != null) {
            evilSeagull$handleDropLogic();
            return;
        }

        if (evilSeagull$isHovering && evilSeagull$hoverPosition != null) {
            evilSeagull$handleHoveringPhase();
            return;
        }

        if (evilSeagull$targetItemWorldPos != null && seagull.getMainHandItem().isEmpty()) {
            seagull.setFlying(true);
            seagull.getMoveControl().setWantedPosition(
                evilSeagull$targetItemWorldPos.x,
                evilSeagull$targetItemWorldPos.y + 0.5,
                evilSeagull$targetItemWorldPos.z,
                1.5F
            );

            if (seagull.position().distanceToSqr(evilSeagull$targetItemWorldPos) < 2.0) {
                ItemStack stolenItem = CreateCompat.extractItemFromBelt(
                    seagull.level(),
                    evilSeagull$targetBeltController,
                    evilSeagull$targetItemIndex,
                    this::evilSeagull$isBeltItemBlacklisted
                );

                if (!stolenItem.isEmpty()) {
                    evilSeagull$handleStolenBeltItem(stolenItem, BlockPos.containing(evilSeagull$targetItemWorldPos));
                } else {
                    evilSeagull$stealingFromBelt = false;
                    evilSeagull$targetBeltController = null;
                    evilSeagull$targetItemWorldPos = null;
                    evilSeagull$targetItemIndex = -1;
                }
            }
        }
    }

    @Unique
    private SophisticatedBackpacksCompat.BackpackBlockInfo evilSeagull$findClosestBackpack(List<SophisticatedBackpacksCompat.BackpackBlockInfo> backpacks) {
        SophisticatedBackpacksCompat.BackpackBlockInfo closest = null;
        double closestDist = Double.MAX_VALUE;

        for (SophisticatedBackpacksCompat.BackpackBlockInfo info : backpacks) {
            double dist = seagull.distanceToSqr(Vec3.atCenterOf(info.pos()));
            if (dist < closestDist) {
                closestDist = dist;
                closest = info;
            }
        }
        return closest;
    }

    @Unique
    private AppliedEnergisticsCompat.MEInterfaceInfo evilSeagull$findClosestMEInterface(List<AppliedEnergisticsCompat.MEInterfaceInfo> interfaces) {
        AppliedEnergisticsCompat.MEInterfaceInfo closest = null;
        double closestDist = Double.MAX_VALUE;

        for (AppliedEnergisticsCompat.MEInterfaceInfo info : interfaces) {
            double dist = seagull.distanceToSqr(Vec3.atCenterOf(info.pos));
            if (dist < closestDist) {
                closestDist = dist;
                closest = info;
            }
        }
        return closest;
    }

    @Unique
    private RefinedStorageCompat.RSInterfaceInfo evilSeagull$findClosestRSInterface(List<RefinedStorageCompat.RSInterfaceInfo> interfaces) {
        RefinedStorageCompat.RSInterfaceInfo closest = null;
        double closestDist = Double.MAX_VALUE;

        for (RefinedStorageCompat.RSInterfaceInfo info : interfaces) {
            double dist = seagull.distanceToSqr(Vec3.atCenterOf(info.pos));
            if (dist < closestDist) {
                closestDist = dist;
                closest = info;
            }
        }
        return closest;
    }

    @Unique
    private CreateCompat.BeltItemInfo evilSeagull$findClosestBeltItem(List<CreateCompat.BeltItemInfo> beltItems) {
        CreateCompat.BeltItemInfo closest = null;
        double closestDist = Double.MAX_VALUE;

        for (CreateCompat.BeltItemInfo info : beltItems) {
            double dist = seagull.position().distanceToSqr(info.itemWorldPos);
            if (dist < closestDist) {
                closestDist = dist;
                closest = info;
            }
        }
        return closest;
    }
}
