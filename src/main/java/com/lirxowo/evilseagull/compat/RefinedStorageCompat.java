package com.lirxowo.evilseagull.compat;

import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.Collection;
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

        private static final Actor SEAGULL_ACTOR = () -> "EvilSeagull";

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
                Network network = interfaceBE.getNetworkForItem();
                if (network == null) {
                    return false;
                }

                StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
                boolean stealAnyItem = EvilSeagullConfig.RS_STEAL_ANY_ITEM.get();

                Collection<ResourceAmount> resources = storage.getAll();
                for (ResourceAmount resourceAmount : resources) {
                    if (resourceAmount.resource() instanceof ItemResource itemResource) {
                        ItemStack stack = itemResource.toItemStack();
                        if (!blacklistChecker.test(stack)) {
                            if (stealAnyItem || stack.has(DataComponents.FOOD)) {
                                return true;
                            }
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
                Network network = interfaceBE.getNetworkForItem();
                if (network == null) {
                    return ItemStack.EMPTY;
                }

                EnergyNetworkComponent energyComponent = network.getComponent(EnergyNetworkComponent.class);
                int energyCost = EvilSeagullConfig.RS_ENERGY_PER_STEAL.get();
                if (energyComponent.getStored() < energyCost) {
                    return ItemStack.EMPTY;
                }

                StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
                boolean stealAnyItem = EvilSeagullConfig.RS_STEAL_ANY_ITEM.get();

                List<ItemResource> validItems = new ArrayList<>();

                Collection<ResourceAmount> resources = storage.getAll();
                for (ResourceAmount resourceAmount : resources) {
                    if (resourceAmount.resource() instanceof ItemResource itemResource) {
                        ItemStack stack = itemResource.toItemStack();
                        if (!blacklistChecker.test(stack)) {
                            if (stealAnyItem || stack.has(DataComponents.FOOD)) {
                                validItems.add(itemResource);
                            }
                        }
                    }
                }

                if (validItems.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                int randomIndex = validItems.size() <= 1 ? 0 : (int) (Math.random() * validItems.size());
                ItemResource selectedResource = validItems.get(randomIndex);

                long extracted = storage.extract(selectedResource, 1, Action.EXECUTE, SEAGULL_ACTOR);

                if (extracted > 0) {
                    energyComponent.getStored(); // energy is consumed by network naturally
                    return selectedResource.toItemStack(extracted);
                }

            } catch (Throwable e) {
            }

            return ItemStack.EMPTY;
        }
    }
}
