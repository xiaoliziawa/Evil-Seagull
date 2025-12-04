package com.lirxowo.evilseagull.compat;

import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SophisticatedBackpacksCompat {

    private static final String MODID = "sophisticatedbackpacks";
    private static boolean isLoaded = false;
    private static boolean checkedLoad = false;

    public static boolean isModLoaded() {
        if (!checkedLoad) {
            isLoaded = ModList.get().isLoaded(MODID);
            checkedLoad = true;
        }
        return isLoaded;
    }

    public static boolean hasItemsInBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_SOPHISTICATED_BACKPACKS.get()) {
            return false;
        }

        try {
            return SophisticatedBackpacksHandler.hasItemsInBackpacks(player, blacklistChecker);
        } catch (Throwable e) {
            return false;
        }
    }

    public static ItemStack extractItemFromBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_SOPHISTICATED_BACKPACKS.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return SophisticatedBackpacksHandler.extractItemFromBackpacks(player, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    public static List<BackpackBlockInfo> findNearbyBackpackBlocksWithItems(Level level, BlockPos center, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_PLACED_BACKPACKS.get()) {
            return List.of();
        }

        try {
            return SophisticatedBackpacksHandler.findNearbyBackpackBlocksWithItems(level, center, blacklistChecker);
        } catch (Throwable e) {
            return List.of();
        }
    }

    public static ItemStack extractItemFromBackpackBlock(Level level, BlockPos pos, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_PLACED_BACKPACKS.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return SophisticatedBackpacksHandler.extractItemFromBackpackBlock(level, pos, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    public record BackpackBlockInfo(BlockPos pos) {
    }

    private static class SophisticatedBackpacksHandler {

        static boolean hasItemsInBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (isBackpackItem(stack)) {
                    if (hasItemInBackpack(stack, blacklistChecker)) {
                        return true;
                    }
                }
            }
            return false;
        }

        static ItemStack extractItemFromBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (isBackpackItem(stack)) {
                    ItemStack item = extractItemFromBackpack(stack, blacklistChecker);
                    if (!item.isEmpty()) {
                        return item;
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        static boolean isBackpackItem(ItemStack stack) {
            if (stack.isEmpty()) return false;
            try {
                return stack.getItem() instanceof BackpackItem;
            } catch (Throwable e) {
                return false;
            }
        }

        private static Optional<IStorageWrapper> getStorageWrapper(ItemStack backpackStack) {
            try {
                return backpackStack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).resolve().map(wrapper -> wrapper);
            } catch (Throwable e) {
                return Optional.empty();
            }
        }

        private static List<Integer> findValidSlots(IItemHandler inventory, Predicate<ItemStack> blacklistChecker) {
            List<Integer> validSlots = new ArrayList<>();
            boolean stealAnyItem = EvilSeagullConfig.BACKPACK_STEAL_ANY_ITEM.get();
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack slotStack = inventory.getStackInSlot(slot);
                if (isValidItem(slotStack, blacklistChecker, stealAnyItem)) {
                    validSlots.add(slot);
                }
            }
            return validSlots;
        }

        private static boolean isValidItem(ItemStack stack, Predicate<ItemStack> blacklistChecker, boolean stealAnyItem) {
            if (stack.isEmpty() || blacklistChecker.test(stack)) {
                return false;
            }
            return stealAnyItem || stack.isEdible();
        }

        private static boolean hasValidItem(IItemHandler inventory, Predicate<ItemStack> blacklistChecker) {
            boolean stealAnyItem = EvilSeagullConfig.BACKPACK_STEAL_ANY_ITEM.get();
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                if (isValidItem(inventory.getStackInSlot(slot), blacklistChecker, stealAnyItem)) {
                    return true;
                }
            }
            return false;
        }

        private static ItemStack extractRandomItem(IItemHandler inventory, List<Integer> validSlots) {
            if (validSlots.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int randomIndex = validSlots.size() <= 1 ? 0 : (int) (Math.random() * validSlots.size());
            int selectedSlot = validSlots.get(randomIndex);
            return inventory.extractItem(selectedSlot, 1, false);
        }

        static boolean hasItemInBackpack(ItemStack backpackStack, Predicate<ItemStack> blacklistChecker) {
            Optional<IStorageWrapper> wrapperOpt = getStorageWrapper(backpackStack);
            if (wrapperOpt.isPresent()) {
                IItemHandler inventory = wrapperOpt.get().getInventoryHandler();
                return hasValidItem(inventory, blacklistChecker);
            }
            return false;
        }

        static ItemStack extractItemFromBackpack(ItemStack backpackStack, Predicate<ItemStack> blacklistChecker) {
            Optional<IStorageWrapper> wrapperOpt = getStorageWrapper(backpackStack);
            if (wrapperOpt.isPresent()) {
                IItemHandler inventory = wrapperOpt.get().getInventoryHandler();
                List<Integer> validSlots = findValidSlots(inventory, blacklistChecker);
                return extractRandomItem(inventory, validSlots);
            }
            return ItemStack.EMPTY;
        }

        static List<BackpackBlockInfo> findNearbyBackpackBlocksWithItems(Level level, BlockPos center, Predicate<ItemStack> blacklistChecker) {
            List<BackpackBlockInfo> result = new ArrayList<>();
            int range = EvilSeagullConfig.PLACED_BACKPACK_SEARCH_RANGE.get();

            Vec3 centerVec = Vec3.atCenterOf(center);
            AABB searchBox = new AABB(
                centerVec.x - range, centerVec.y - range, centerVec.z - range,
                centerVec.x + range, centerVec.y + range, centerVec.z + range
            );

            int chunkRange = (range >> 4) + 1;
            int centerChunkX = center.getX() >> 4;
            int centerChunkZ = center.getZ() >> 4;

            for (int cx = -chunkRange; cx <= chunkRange; cx++) {
                for (int cz = -chunkRange; cz <= chunkRange; cz++) {
                    int chunkX = centerChunkX + cx;
                    int chunkZ = centerChunkZ + cz;
                    if (level.hasChunkAt(new BlockPos(chunkX << 4, 0, chunkZ << 4))) {
                        var chunk = level.getChunkAt(new BlockPos(chunkX << 4, 0, chunkZ << 4));
                        for (BlockEntity be : chunk.getBlockEntities().values()) {
                            if (be instanceof BackpackBlockEntity backpackBE) {
                                if (searchBox.contains(Vec3.atCenterOf(be.getBlockPos()))) {
                                    try {
                                        if (hasItemInBackpackBlock(backpackBE, blacklistChecker)) {
                                            result.add(new BackpackBlockInfo(be.getBlockPos()));
                                        }
                                    } catch (Throwable e) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return result;
        }

        static boolean hasItemInBackpackBlock(BackpackBlockEntity backpackBE, Predicate<ItemStack> blacklistChecker) {
            try {
                IStorageWrapper wrapper = backpackBE.getBackpackWrapper();
                IItemHandler inventory = wrapper.getInventoryHandler();
                return hasValidItem(inventory, blacklistChecker);
            } catch (Throwable e) {
                return false;
            }
        }

        static ItemStack extractItemFromBackpackBlock(Level level, BlockPos pos, Predicate<ItemStack> blacklistChecker) {
            try {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BackpackBlockEntity backpackBE) {
                    IStorageWrapper wrapper = backpackBE.getBackpackWrapper();
                    IItemHandler inventory = wrapper.getInventoryHandler();
                    List<Integer> validSlots = findValidSlots(inventory, blacklistChecker);
                    return extractRandomItem(inventory, validSlots);
                }
            } catch (Throwable e) {
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
    }
}
