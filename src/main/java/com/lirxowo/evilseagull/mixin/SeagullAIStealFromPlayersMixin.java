package com.lirxowo.evilseagull.mixin;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.lirxowo.evilseagull.advancement.ESAdvancementTriggerRegistry;
import com.lirxowo.evilseagull.compat.AppliedEnergisticsCompat;
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

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void evilSeagull$onCanUse(CallbackInfoReturnable<Boolean> cir) {
        evilSeagull$targetMEInterface = null;
        evilSeagull$stealingFromME = false;
        evilSeagull$targetBackpackBlock = null;
        evilSeagull$stealingFromBackpackBlock = false;
        evilSeagull$targetRSInterface = null;
        evilSeagull$stealingFromRS = false;
    }

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$afterCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && seagull.getMainHandItem().isEmpty()) {
            if (!AMConfig.seagullStealing || seagull.stealCooldown > 0 || seagull.isSitting()) {
                return;
            }

            // 检查放置的精妙背包方块
            if (SophisticatedBackpacksCompat.isModLoaded() && EvilSeagullConfig.STEAL_FROM_PLACED_BACKPACKS.get()) {
                List<SophisticatedBackpacksCompat.BackpackBlockInfo> backpacks = SophisticatedBackpacksCompat.findNearbyBackpackBlocksWithFood(seagull.level(), seagull.blockPosition(), this::evilSeagull$isBlacklisted);

                if (!backpacks.isEmpty()) {
                    SophisticatedBackpacksCompat.BackpackBlockInfo closest = null;
                    double closestDist = Double.MAX_VALUE;

                    for (SophisticatedBackpacksCompat.BackpackBlockInfo info : backpacks) {
                        double dist = seagull.distanceToSqr(Vec3.atCenterOf(info.pos()));
                        if (dist < closestDist) {
                            closestDist = dist;
                            closest = info;
                        }
                    }

                    if (closest != null) {
                        evilSeagull$targetBackpackBlock = closest.pos();
                        evilSeagull$stealingFromBackpackBlock = true;
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }

            // 检查 ME 接口
            if (AppliedEnergisticsCompat.isModLoaded() && EvilSeagullConfig.STEAL_FROM_ME_INTERFACE.get()) {
                List<AppliedEnergisticsCompat.MEInterfaceInfo> interfaces = AppliedEnergisticsCompat.findNearbyMEInterfacesWithFood(seagull.level(), seagull.blockPosition(), this::evilSeagull$isBlacklisted);

                if (!interfaces.isEmpty()) {
                    AppliedEnergisticsCompat.MEInterfaceInfo closest = null;
                    double closestDist = Double.MAX_VALUE;

                    for (AppliedEnergisticsCompat.MEInterfaceInfo info : interfaces) {
                        double dist = seagull.distanceToSqr(Vec3.atCenterOf(info.pos));
                        if (dist < closestDist) {
                            closestDist = dist;
                            closest = info;
                        }
                    }

                    if (closest != null) {
                        evilSeagull$targetMEInterface = closest.pos;
                        evilSeagull$stealingFromME = true;
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }

            // 检查 RS 接口
            if (RefinedStorageCompat.isModLoaded() && EvilSeagullConfig.STEAL_FROM_RS_INTERFACE.get()) {
                List<RefinedStorageCompat.RSInterfaceInfo> interfaces = RefinedStorageCompat.findNearbyRSInterfacesWithFood(seagull.level(), seagull.blockPosition(), this::evilSeagull$isBlacklisted);

                if (!interfaces.isEmpty()) {
                    RefinedStorageCompat.RSInterfaceInfo closest = null;
                    double closestDist = Double.MAX_VALUE;

                    for (RefinedStorageCompat.RSInterfaceInfo info : interfaces) {
                        double dist = seagull.distanceToSqr(Vec3.atCenterOf(info.pos));
                        if (dist < closestDist) {
                            closestDist = dist;
                            closest = info;
                        }
                    }

                    if (closest != null) {
                        evilSeagull$targetRSInterface = closest.pos;
                        evilSeagull$stealingFromRS = true;
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

        if (SophisticatedBackpacksCompat.hasFoodsInBackpacks(player, this::evilSeagull$isBlacklisted)) {
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

        ItemStack backpackFood = SophisticatedBackpacksCompat.extractFoodFromBackpacks(player, this::evilSeagull$isBlacklisted);
        if (!backpackFood.isEmpty()) {
            if (cir.getReturnValue().isEmpty()) {
                seagull.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.PLAYERS, 1.0F, 1.0F);

                if (backpackFood.is(Items.BAKED_POTATO)) {
                    evilSeagull$triggerAdvancementForNearbyPlayers(player.blockPosition());
                }

                cir.setReturnValue(backpackFood);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void evilSeagull$onTick(CallbackInfo ci) {
        if (evilSeagull$stealingFromBackpackBlock && evilSeagull$targetBackpackBlock != null) {
            Vec3 targetVec = Vec3.atCenterOf(evilSeagull$targetBackpackBlock);
            evilSeagull$moveToTarget(targetVec);

            if (seagull.distanceToSqr(targetVec) < 4.0 && seagull.getMainHandItem().isEmpty()) {
                ItemStack stolenFood = SophisticatedBackpacksCompat.extractFoodFromBackpackBlock(seagull.level(), evilSeagull$targetBackpackBlock, this::evilSeagull$isBlacklisted);

                if (!stolenFood.isEmpty()) {
                    evilSeagull$handleStolenFood(stolenFood, evilSeagull$targetBackpackBlock);
                } else {
                    evilSeagull$stealingFromBackpackBlock = false;
                    evilSeagull$targetBackpackBlock = null;
                }
            }

            evilSeagull$handleFleeLogic();

            ci.cancel();
            return;
        }

        if (evilSeagull$stealingFromME && evilSeagull$targetMEInterface != null) {
            Vec3 targetVec = Vec3.atCenterOf(evilSeagull$targetMEInterface);
            evilSeagull$moveToTarget(targetVec);

            if (seagull.distanceToSqr(targetVec) < 4.0 && seagull.getMainHandItem().isEmpty()) {
                ItemStack stolenFood = AppliedEnergisticsCompat.extractFoodFromMEInterface(seagull.level(), evilSeagull$targetMEInterface, this::evilSeagull$isBlacklisted);

                if (!stolenFood.isEmpty()) {
                    evilSeagull$handleStolenFood(stolenFood, evilSeagull$targetMEInterface);
                } else {
                    evilSeagull$stealingFromME = false;
                    evilSeagull$targetMEInterface = null;
                }
            }

            evilSeagull$handleFleeLogic();

            ci.cancel();
        }

        if (evilSeagull$stealingFromRS && evilSeagull$targetRSInterface != null) {
            Vec3 targetVec = Vec3.atCenterOf(evilSeagull$targetRSInterface);
            evilSeagull$moveToTarget(targetVec);

            if (seagull.distanceToSqr(targetVec) < 4.0 && seagull.getMainHandItem().isEmpty()) {
                ItemStack stolenFood = RefinedStorageCompat.extractFoodFromRSInterface(seagull.level(), evilSeagull$targetRSInterface, this::evilSeagull$isBlacklisted);

                if (!stolenFood.isEmpty()) {
                    evilSeagull$handleStolenFood(stolenFood, evilSeagull$targetRSInterface);
                } else {
                    evilSeagull$stealingFromRS = false;
                    evilSeagull$targetRSInterface = null;
                }
            }

            evilSeagull$handleFleeLogic();

            ci.cancel();
        }
    }

    @Inject(method = "canContinueToUse", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$onCanContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        if (evilSeagull$stealingFromBackpackBlock) {
            boolean canContinue = evilSeagull$targetBackpackBlock != null && (seagull.getMainHandItem().isEmpty() || fleeTime > 0);
            cir.setReturnValue(canContinue);
            return;
        }
        if (evilSeagull$stealingFromME) {
            boolean canContinue = evilSeagull$targetMEInterface != null && (seagull.getMainHandItem().isEmpty() || fleeTime > 0);
            cir.setReturnValue(canContinue);
            return;
        }
        if (evilSeagull$stealingFromRS) {
            boolean canContinue = evilSeagull$targetRSInterface != null && (seagull.getMainHandItem().isEmpty() || fleeTime > 0);
            cir.setReturnValue(canContinue);
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void evilSeagull$onStop(CallbackInfo ci) {
        evilSeagull$targetMEInterface = null;
        evilSeagull$stealingFromME = false;
        evilSeagull$targetBackpackBlock = null;
        evilSeagull$stealingFromBackpackBlock = false;
        evilSeagull$targetRSInterface = null;
        evilSeagull$stealingFromRS = false;
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
    private void evilSeagull$handleStolenFood(ItemStack stolenFood, BlockPos sourcePos) {
        seagull.peck();
        seagull.setItemInHand(InteractionHand.MAIN_HAND, stolenFood);
        fleeTime = 60;

        seagull.level().playSound(null, sourcePos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stolenFood.is(Items.BAKED_POTATO)) {
            evilSeagull$triggerAdvancementForNearbyPlayers(sourcePos);
        }

        int baseCooldown = 1500 + seagull.getRandom().nextInt(1500);
        int modifier = EvilSeagullConfig.STEAL_COOLDOWN_MODIFIER.get();
        seagull.stealCooldown = baseCooldown * modifier / 100;
    }

    @Unique
    private void evilSeagull$handleFleeLogic() {
        if (fleeTime > 0) {
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
        seagull.getMoveControl().setWantedPosition(targetVec.x, targetVec.y + 1, targetVec.z, 1.2F);
    }
}
