package com.lirxowo.evilseagull.compat;

import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RefinedStorageCompat {

    private static final String MODID = "refinedstorage";
    private static boolean isLoaded = false;
    private static boolean checkedLoad = false;

    public static boolean isModLoaded() {
        if (!checkedLoad) {
            isLoaded = ModList.get().isLoaded(MODID);
            checkedLoad = true;
        }
        return isLoaded;
    }

    public static class RSInterfaceInfo {
        public final BlockPos pos;
        public final boolean hasFood;

        public RSInterfaceInfo(BlockPos pos, boolean hasFood) {
            this.pos = pos;
            this.hasFood = hasFood;
        }
    }

    public static List<RSInterfaceInfo> findNearbyRSInterfacesWithFood(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
        List<RSInterfaceInfo> interfaces = new ArrayList<>();

        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_RS_INTERFACE.get()) {
            return interfaces;
        }

        try {
            return RSHandler.findNearbyRSInterfacesWithFood(level, centerPos, blacklistChecker);
        } catch (Throwable e) {
            return interfaces;
        }
    }

    public static ItemStack extractFoodFromRSInterface(Level level, BlockPos interfacePos, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_RS_INTERFACE.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return RSHandler.extractFoodFromRSInterface(level, interfacePos, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    private static class RSHandler {

        static List<RSInterfaceInfo> findNearbyRSInterfacesWithFood(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
            List<RSInterfaceInfo> interfaces = new ArrayList<>();
            int range = EvilSeagullConfig.RS_INTERFACE_SEARCH_RANGE.get();

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos checkPos = centerPos.offset(x, y, z);
                        BlockEntity blockEntity = level.getBlockEntity(checkPos);

                        if (blockEntity instanceof InterfaceBlockEntity interfaceBE) {
                            if (hasFoodInRSInterface(interfaceBE, blacklistChecker)) {
                                interfaces.add(new RSInterfaceInfo(checkPos, true));
                            }
                        }
                    }
                }
            }

            return interfaces;
        }

        static boolean hasFoodInRSInterface(InterfaceBlockEntity interfaceBE, Predicate<ItemStack> blacklistChecker) {
            try {
                var node = interfaceBE.getNode();

                if (node == null) {
                    return false;
                }

                INetwork network = node.getNetwork();
                if (network == null || !network.canRun()) {
                    return false;
                }

                var storageCache = network.getItemStorageCache();
                var stackList = storageCache.getList();

                for (StackListEntry<ItemStack> entry : stackList.getStacks()) {
                    ItemStack stack = entry.getStack();
                    if (stack.isEdible() && !blacklistChecker.test(stack)) {
                        return true;
                    }
                }
            } catch (Throwable e) {
            }
            return false;
        }

        static ItemStack extractFoodFromRSInterface(Level level, BlockPos interfacePos, Predicate<ItemStack> blacklistChecker) {
            BlockEntity blockEntity = level.getBlockEntity(interfacePos);

            if (!(blockEntity instanceof InterfaceBlockEntity interfaceBE)) {
                return ItemStack.EMPTY;
            }

            try {
                var node = interfaceBE.getNode();

                if (node == null) {
                    return null;
                }

                INetwork network = node.getNetwork();
                if (network == null || !network.canRun()) {
                    return ItemStack.EMPTY;
                }

                // 检查能量是否足够
                var energyStorage = network.getEnergyStorage();
                int energyCost = EvilSeagullConfig.RS_ENERGY_PER_STEAL.get();
                if (energyStorage.getEnergyStored() < energyCost) {
                    return ItemStack.EMPTY;
                }

                var storageCache = network.getItemStorageCache();
                var stackList = storageCache.getList();

                List<ItemStack> foodItems = new ArrayList<>();

                for (StackListEntry<ItemStack> entry : stackList.getStacks()) {
                    ItemStack stack = entry.getStack();
                    if (stack.isEdible() && !blacklistChecker.test(stack)) {
                        foodItems.add(stack.copy());
                    }
                }

                if (foodItems.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                int randomIndex = foodItems.size() <= 1 ? 0 : (int) (Math.random() * foodItems.size());
                ItemStack selectedFood = foodItems.get(randomIndex);

                // 从网络中提取食物
                ItemStack extracted = network.extractItem(selectedFood, 1, Action.PERFORM);

                if (!extracted.isEmpty()) {
                    // 消耗能量
                    energyStorage.extractEnergy(energyCost, false);
                    return extracted;
                }

            } catch (Throwable e) {
            }

            return ItemStack.EMPTY;
        }
    }
}
