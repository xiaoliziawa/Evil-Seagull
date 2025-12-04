package com.lirxowo.evilseagull.compat;

import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class CreateCompat {

    private static final String MODID = "create";
    private static boolean isLoaded = false;
    private static boolean checkedLoad = false;

    public static boolean isModLoaded() {
        if (!checkedLoad) {
            isLoaded = ModList.get().isLoaded(MODID);
            checkedLoad = true;
        }
        return isLoaded;
    }

    public static class BeltItemInfo {
        public final BlockPos beltControllerPos;
        public final Vec3 itemWorldPos;
        public final boolean hasItem;
        public final int itemIndex;

        public BeltItemInfo(BlockPos beltControllerPos, Vec3 itemWorldPos, boolean hasItem, int itemIndex) {
            this.beltControllerPos = beltControllerPos;
            this.itemWorldPos = itemWorldPos;
            this.hasItem = hasItem;
            this.itemIndex = itemIndex;
        }
    }

    public static List<BeltItemInfo> findNearbyBeltsWithItems(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
        List<BeltItemInfo> belts = new ArrayList<>();

        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_CREATE_BELT.get()) {
            return belts;
        }

        try {
            return CreateHandler.findNearbyBeltsWithItems(level, centerPos, blacklistChecker);
        } catch (Throwable e) {
            return belts;
        }
    }

    public static ItemStack extractItemFromBelt(Level level, BlockPos controllerPos, int itemIndex, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_CREATE_BELT.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return CreateHandler.extractItemFromBelt(level, controllerPos, itemIndex, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    private static class CreateHandler {

        static List<BeltItemInfo> findNearbyBeltsWithItems(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
            List<BeltItemInfo> results = new ArrayList<>();
            int range = EvilSeagullConfig.CREATE_BELT_SEARCH_RANGE.get();

            Set<BlockPos> checkedControllers = new HashSet<>();

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos checkPos = centerPos.offset(x, y, z);
                        BlockState state = level.getBlockState(checkPos);

                        if (state.getBlock() instanceof BeltBlock) {
                            BlockEntity blockEntity = level.getBlockEntity(checkPos);
                            if (blockEntity instanceof BeltBlockEntity beltBE) {
                                BeltBlockEntity controllerBE = beltBE.getControllerBE();
                                if (controllerBE == null) {
                                    continue;
                                }

                                BlockPos controllerPos = controllerBE.getBlockPos();

                                if (checkedControllers.contains(controllerPos)) {
                                    continue;
                                }
                                checkedControllers.add(controllerPos);

                                BeltInventory inventory = controllerBE.getInventory();
                                if (inventory == null) {
                                    continue;
                                }

                                List<TransportedItemStack> items = inventory.getTransportedItems();
                                for (int i = 0; i < items.size(); i++) {
                                    TransportedItemStack transportedStack = items.get(i);
                                    if (transportedStack != null && !transportedStack.stack.isEmpty()) {
                                        if (!blacklistChecker.test(transportedStack.stack)) {
                                            Vec3 itemWorldPos = BeltHelper.getVectorForOffset(controllerBE, transportedStack.beltPosition);

                                            double distSq = itemWorldPos.distanceToSqr(Vec3.atCenterOf(centerPos));
                                            if (distSq <= range * range) {
                                                results.add(new BeltItemInfo(controllerPos, itemWorldPos, true, i));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return results;
        }

        static ItemStack extractItemFromBelt(Level level, BlockPos controllerPos, int itemIndex, Predicate<ItemStack> blacklistChecker) {
            BlockEntity blockEntity = level.getBlockEntity(controllerPos);

            if (!(blockEntity instanceof BeltBlockEntity beltBE)) {
                return ItemStack.EMPTY;
            }

            BeltBlockEntity controllerBE = beltBE.getControllerBE();
            if (controllerBE == null || !controllerBE.getBlockPos().equals(controllerPos)) {
                controllerBE = beltBE;
            }

            try {
                BeltInventory inventory = controllerBE.getInventory();
                if (inventory == null) {
                    return ItemStack.EMPTY;
                }

                List<TransportedItemStack> items = inventory.getTransportedItems();

                if (itemIndex < 0 || itemIndex >= items.size()) {
                    return ItemStack.EMPTY;
                }

                TransportedItemStack selectedItem = items.get(itemIndex);

                if (selectedItem == null || selectedItem.stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                if (blacklistChecker.test(selectedItem.stack)) {
                    return ItemStack.EMPTY;
                }

                ItemStack extracted = selectedItem.stack.copy();
                extracted.setCount(1);

                if (selectedItem.stack.getCount() <= 1) {
                    items.remove(itemIndex);
                } else {
                    selectedItem.stack.shrink(1);
                }

                controllerBE.setChanged();
                controllerBE.sendData();
                controllerBE.notifyUpdate();

                return extracted;

            } catch (Throwable e) {
            }

            return ItemStack.EMPTY;
        }
    }
}
