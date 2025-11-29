package com.lirxowo.evilseagull.mixin;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.lirxowo.evilseagull.compat.AppliedEnergisticsCompat;
import com.lirxowo.evilseagull.compat.SophisticatedBackpacksCompat;
import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

@Mixin(value = SeagullAIStealFromPlayers.class, remap = false)
public abstract class SeagullAIStealFromPlayersMixin extends Goal {

    @Shadow
    @Final
    private EntitySeagull seagull;

    @Shadow
    private Player target;

    @Shadow
    private Vec3 fleeVec;

    @Shadow
    private int fleeTime;

    @Unique
    private BlockPos evilSeagull$targetMEInterface = null;

    @Unique
    private boolean evilSeagull$stealingFromME = false;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void evilSeagull$onCanUse(CallbackInfoReturnable<Boolean> cir) {
        evilSeagull$targetMEInterface = null;
        evilSeagull$stealingFromME = false;
    }

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$afterCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && seagull.getMainHandItem().isEmpty()) {
            if (!AMConfig.seagullStealing || seagull.stealCooldown > 0 || seagull.isSitting()) {
                return;
            }

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
                    }
                }
            }
        }
    }

    @Inject(method = "hasFoods", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$onHasFoods(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }

        if (SophisticatedBackpacksCompat.hasFoodsInBackpacks(player, this::evilSeagull$isBlacklisted)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getFoodItemFrom", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$onGetFoodItemFrom(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty()) {
            if (EvilSeagullConfig.PRIORITIZE_PLAYER_INVENTORY.get()) {
                return;
            }
        }

        ItemStack backpackFood = SophisticatedBackpacksCompat.extractFoodFromBackpacks(player, this::evilSeagull$isBlacklisted);
        if (!backpackFood.isEmpty()) {
            if (cir.getReturnValue().isEmpty()) {
                cir.setReturnValue(backpackFood);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void evilSeagull$onTick(CallbackInfo ci) {
        if (evilSeagull$stealingFromME && evilSeagull$targetMEInterface != null) {
            seagull.setFlying(true);

            Vec3 targetVec = Vec3.atCenterOf(evilSeagull$targetMEInterface);
            seagull.getMoveControl().setWantedPosition(targetVec.x, targetVec.y + 1, targetVec.z, 1.2F);

            if (seagull.distanceToSqr(targetVec) < 4.0 && seagull.getMainHandItem().isEmpty()) {
                ItemStack stolenFood = AppliedEnergisticsCompat.extractFoodFromMEInterface(seagull.level(), evilSeagull$targetMEInterface, this::evilSeagull$isBlacklisted);

                if (!stolenFood.isEmpty()) {
                    seagull.peck();
                    seagull.setItemInHand(InteractionHand.MAIN_HAND, stolenFood);
                    fleeTime = 60;

                    int baseCooldown = 1500 + seagull.getRandom().nextInt(1500);
                    int modifier = EvilSeagullConfig.STEAL_COOLDOWN_MODIFIER.get();
                    seagull.stealCooldown = baseCooldown * modifier / 100;
                } else {
                    evilSeagull$stealingFromME = false;
                    evilSeagull$targetMEInterface = null;
                }
            }

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

            ci.cancel();
        }
    }

    @Inject(method = "canContinueToUse", at = @At("RETURN"), cancellable = true)
    private void evilSeagull$onCanContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        if (evilSeagull$stealingFromME) {
            boolean canContinue = evilSeagull$targetMEInterface != null && (seagull.getMainHandItem().isEmpty() || fleeTime > 0);
            cir.setReturnValue(canContinue);
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void evilSeagull$onStop(CallbackInfo ci) {
        evilSeagull$targetMEInterface = null;
        evilSeagull$stealingFromME = false;
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
}
