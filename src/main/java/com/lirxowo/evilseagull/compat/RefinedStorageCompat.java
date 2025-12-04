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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
        public final boolean hasItem;

        public RSInterfaceInfo(BlockPos pos, boolean hasItem) {
            this.pos = pos;
            this.hasItem = hasItem;
        }
    }

    public static List<RSInterfaceInfo> findNearbyRSInterfacesWithItems(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
        List<RSInterfaceInfo> interfaces = new ArrayList<>();

        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_RS_INTERFACE.get()) {
            return interfaces;
        }

        try {
            return RSHandler.findNearbyRSInterfacesWithItems(level, centerPos, blacklistChecker);
        } catch (Throwable e) {
            return interfaces;
        }
    }

    public static ItemStack extractItemFromRSInterface(Level level, BlockPos interfacePos, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_RS_INTERFACE.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return RSHandler.extractItemFromRSInterface(level, interfacePos, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    private static class RSHandler {

        static List<RSInterfaceInfo> findNearbyRSInterfacesWithItems(Level level, BlockPos centerPos, Predicate<ItemStack> blacklistChecker) {
            List<RSInterfaceInfo> interfaces = new ArrayList<>();
            int range = EvilSeagullConfig.RS_INTERFACE_SEARCH_RANGE.get();

            Vec3 centerVec = Vec3.atCenterOf(centerPos);
            AABB searchBox = new AABB(
                centerVec.x - range, centerVec.y - range, centerVec.z - range,
                centerVec.x + range, centerVec.y + range, centerVec.z + range
            );

            int chunkRange = (range >> 4) + 1;
            int centerChunkX = centerPos.getX() >> 4;
            int centerChunkZ = centerPos.getZ() >> 4;

            for (int cx = -chunkRange; cx <= chunkRange; cx++) {
                for (int cz = -chunkRange; cz <= chunkRange; cz++) {
                    int chunkX = centerChunkX + cx;
                    int chunkZ = centerChunkZ + cz;
                    if (level.hasChunkAt(new BlockPos(chunkX << 4, 0, chunkZ << 4))) {
                        var chunk = level.getChunkAt(new BlockPos(chunkX << 4, 0, chunkZ << 4));
                        for (BlockEntity be : chunk.getBlockEntities().values()) {
                            if (be instanceof InterfaceBlockEntity interfaceBE) {
                                if (searchBox.contains(Vec3.atCenterOf(be.getBlockPos()))) {
                                    if (hasItemInRSInterface(interfaceBE, blacklistChecker)) {
                                        interfaces.add(new RSInterfaceInfo(be.getBlockPos(), true));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return interfaces;
        }

        static boolean hasItemInRSInterface(InterfaceBlockEntity interfaceBE, Predicate<ItemStack> blacklistChecker) {
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
                boolean stealAnyItem = EvilSeagullConfig.RS_STEAL_ANY_ITEM.get();

                for (StackListEntry<ItemStack> entry : stackList.getStacks()) {
                    ItemStack stack = entry.getStack();
                    if (!blacklistChecker.test(stack)) {
                        if (stealAnyItem || stack.isEdible()) {
                            return true;
                        }
                    }
                }
            } catch (Throwable e) {
            }
            return false;
        }

        static ItemStack extractItemFromRSInterface(Level level, BlockPos interfacePos, Predicate<ItemStack> blacklistChecker) {
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

                var energyStorage = network.getEnergyStorage();
                int energyCost = EvilSeagullConfig.RS_ENERGY_PER_STEAL.get();
                if (energyStorage.getEnergyStored() < energyCost) {
                    return ItemStack.EMPTY;
                }

                var storageCache = network.getItemStorageCache();
                var stackList = storageCache.getList();
                boolean stealAnyItem = EvilSeagullConfig.RS_STEAL_ANY_ITEM.get();

                List<ItemStack> validItems = new ArrayList<>();

                for (StackListEntry<ItemStack> entry : stackList.getStacks()) {
                    ItemStack stack = entry.getStack();
                    if (!blacklistChecker.test(stack)) {
                        if (stealAnyItem || stack.isEdible()) {
                            validItems.add(stack.copy());
                        }
                    }
                }

                if (validItems.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                int randomIndex = validItems.size() <= 1 ? 0 : (int) (Math.random() * validItems.size());
                ItemStack selectedItem = validItems.get(randomIndex);

                ItemStack extracted = network.extractItem(selectedItem, 1, Action.PERFORM);

                if (!extracted.isEmpty()) {
                    energyStorage.extractEnergy(energyCost, false);
                    return extracted;
                }

            } catch (Throwable e) {
            }

            return ItemStack.EMPTY;
        }
    }
}
