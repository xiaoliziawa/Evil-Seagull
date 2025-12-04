package com.lirxowo.evilseagull.compat;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageHelper;
import appeng.blockentity.misc.InterfaceBlockEntity;
import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AppliedEnergisticsCompat {

    private static final String MODID = "ae2";
    private static boolean isLoaded = false;
    private static boolean checkedLoad = false;

    public static boolean isModLoaded() {
        if (!checkedLoad) {
            isLoaded = ModList.get().isLoaded(MODID);
            checkedLoad = true;
        }
        return isLoaded;
    }

    public static class MEInterfaceInfo {
        public final BlockPos pos;
        public final boolean hasItem;

        public MEInterfaceInfo(BlockPos pos, boolean hasItem) {
            this.pos = pos;
            this.hasItem = hasItem;
        }
    }

    public static List<MEInterfaceInfo> findNearbyMEInterfacesWithItems(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
        List<MEInterfaceInfo> interfaces = new ArrayList<>();

        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_ME_INTERFACE.get()) {
            return interfaces;
        }

        try {
            return AE2Handler.findNearbyMEInterfacesWithItems(level, centerPos, blacklistChecker);
        } catch (Throwable e) {
            return interfaces;
        }
    }

    public static ItemStack extractItemFromMEInterface(Level level, BlockPos interfacePos, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_ME_INTERFACE.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return AE2Handler.extractItemFromMEInterface(level, interfacePos, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    private static class AE2Handler {

        static List<MEInterfaceInfo> findNearbyMEInterfacesWithItems(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
            List<MEInterfaceInfo> interfaces = new ArrayList<>();
            int range = EvilSeagullConfig.ME_INTERFACE_SEARCH_RANGE.get();

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos checkPos = centerPos.offset(x, y, z);
                        BlockEntity blockEntity = level.getBlockEntity(checkPos);

                        if (blockEntity instanceof InterfaceBlockEntity interfaceBE) {
                            if (hasItemInMEInterface(interfaceBE, blacklistChecker)) {
                                interfaces.add(new MEInterfaceInfo(checkPos, true));
                            }
                        }
                    }
                }
            }

            return interfaces;
        }

        static boolean hasItemInMEInterface(InterfaceBlockEntity interfaceBE, Predicate<ItemStack> blacklistChecker) {
            try {
                var mainNode = interfaceBE.getMainNode();

                if (mainNode == null || mainNode.getGrid() == null) {
                    return false;
                }

                var gridNode = mainNode.getNode();
                if (gridNode == null || !gridNode.isActive()) {
                    return false;
                }

                var grid = mainNode.getGrid();
                var storageService = grid.getStorageService();
                var cachedInventory = storageService.getCachedInventory();
                boolean stealAnyItem = EvilSeagullConfig.ME_STEAL_ANY_ITEM.get();

                for (var entry : cachedInventory) {
                    var key = entry.getKey();
                    if (key instanceof AEItemKey itemKey) {
                        ItemStack stack = itemKey.toStack();
                        if (!blacklistChecker.test(stack)) {
                            if (stealAnyItem || stack.isEdible()) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Throwable e) {
            }
            return false;
        }

        static ItemStack extractItemFromMEInterface(Level level, BlockPos interfacePos, Predicate<ItemStack> blacklistChecker) {
            BlockEntity blockEntity = level.getBlockEntity(interfacePos);

            if (!(blockEntity instanceof InterfaceBlockEntity interfaceBE)) {
                return ItemStack.EMPTY;
            }

            try {
                var mainNode = interfaceBE.getMainNode();

                if (mainNode == null || mainNode.getGrid() == null) {
                    return ItemStack.EMPTY;
                }

                var gridNode = mainNode.getNode();
                if (gridNode == null || !gridNode.isActive()) {
                    return ItemStack.EMPTY;
                }

                var grid = mainNode.getGrid();
                var storageService = grid.getStorageService();
                var energyService = grid.getEnergyService();
                var inventory = storageService.getInventory();
                boolean stealAnyItem = EvilSeagullConfig.ME_STEAL_ANY_ITEM.get();

                List<AEItemKey> validItems = new ArrayList<>();
                var cachedInventory = storageService.getCachedInventory();

                for (var entry : cachedInventory) {
                    var key = entry.getKey();
                    if (key instanceof AEItemKey itemKey) {
                        ItemStack stack = itemKey.toStack();
                        if (!blacklistChecker.test(stack) && entry.getLongValue() > 0) {
                            if (stealAnyItem || stack.isEdible()) {
                                validItems.add(itemKey);
                            }
                        }
                    }
                }

                if (validItems.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                int randomIndex = validItems.size() <= 1 ? 0 : (int) (Math.random() * validItems.size());
                AEItemKey selectedItem = validItems.get(randomIndex);

                IActionSource source = IActionSource.empty();

                double powerCost = EvilSeagullConfig.ME_POWER_PER_STEAL.get();
                double availablePower = energyService.getStoredPower();
                if (availablePower < powerCost) {
                    return ItemStack.EMPTY;
                }

                long extracted = StorageHelper.poweredExtraction(energyService, inventory, selectedItem, 1, source, Actionable.MODULATE);

                if (extracted > 0) {
                    return selectedItem.toStack((int) extracted);
                }

            } catch (Throwable e) {
            }

            return ItemStack.EMPTY;
        }
    }
}
