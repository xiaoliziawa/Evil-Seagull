package com.lirxowo.evilseagull.compat;

import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public static boolean hasFoodsInBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_SOPHISTICATED_BACKPACKS.get()) {
            return false;
        }

        try {
            return SophisticatedBackpacksHandler.hasFoodsInBackpacks(player, blacklistChecker);
        } catch (Throwable e) {
            return false;
        }
    }

    public static ItemStack extractFoodFromBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_SOPHISTICATED_BACKPACKS.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return SophisticatedBackpacksHandler.extractFoodFromBackpacks(player, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    public static List<BackpackBlockInfo> findNearbyBackpackBlocksWithFood(Level level, BlockPos center, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_PLACED_BACKPACKS.get()) {
            return List.of();
        }

        try {
            return SophisticatedBackpacksHandler.findNearbyBackpackBlocksWithFood(level, center, blacklistChecker);
        } catch (Throwable e) {
            return List.of();
        }
    }

    public static ItemStack extractFoodFromBackpackBlock(Level level, BlockPos pos, Predicate<ItemStack> blacklistChecker) {
        if (!isModLoaded() || !EvilSeagullConfig.STEAL_FROM_PLACED_BACKPACKS.get()) {
            return ItemStack.EMPTY;
        }

        try {
            return SophisticatedBackpacksHandler.extractFoodFromBackpackBlock(level, pos, blacklistChecker);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }

    public record BackpackBlockInfo(BlockPos pos) {
    }

    private static class SophisticatedBackpacksHandler {

        static boolean hasFoodsInBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (isBackpackItem(stack)) {
                    if (hasFoodInBackpack(stack, blacklistChecker)) {
                        return true;
                    }
                }
            }
            return false;
        }

        static ItemStack extractFoodFromBackpacks(Player player, Predicate<ItemStack> blacklistChecker) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (isBackpackItem(stack)) {
                    ItemStack food = extractFoodFromBackpack(stack, blacklistChecker);
                    if (!food.isEmpty()) {
                        return food;
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

        private static List<Integer> findFoodSlots(IItemHandler inventory, Predicate<ItemStack> blacklistChecker) {
            List<Integer> foodSlots = new ArrayList<>();
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack slotStack = inventory.getStackInSlot(slot);
                if (isFoodItem(slotStack, blacklistChecker)) {
                    foodSlots.add(slot);
                }
            }
            return foodSlots;
        }

        private static boolean isFoodItem(ItemStack stack, Predicate<ItemStack> blacklistChecker) {
            return !stack.isEmpty() && stack.isEdible() && !blacklistChecker.test(stack);
        }

        private static boolean hasFood(IItemHandler inventory, Predicate<ItemStack> blacklistChecker) {
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                if (isFoodItem(inventory.getStackInSlot(slot), blacklistChecker)) {
                    return true;
                }
            }
            return false;
        }

        private static ItemStack extractRandomFood(IItemHandler inventory, List<Integer> foodSlots) {
            if (foodSlots.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int randomIndex = foodSlots.size() <= 1 ? 0 : (int) (Math.random() * foodSlots.size());
            int selectedSlot = foodSlots.get(randomIndex);
            return inventory.extractItem(selectedSlot, 1, false);
        }

        static boolean hasFoodInBackpack(ItemStack backpackStack, Predicate<ItemStack> blacklistChecker) {
            Optional<IStorageWrapper> wrapperOpt = getStorageWrapper(backpackStack);
            if (wrapperOpt.isPresent()) {
                IItemHandler inventory = wrapperOpt.get().getInventoryHandler();
                return hasFood(inventory, blacklistChecker);
            }
            return false;
        }

        static ItemStack extractFoodFromBackpack(ItemStack backpackStack, Predicate<ItemStack> blacklistChecker) {
            Optional<IStorageWrapper> wrapperOpt = getStorageWrapper(backpackStack);
            if (wrapperOpt.isPresent()) {
                IItemHandler inventory = wrapperOpt.get().getInventoryHandler();
                List<Integer> foodSlots = findFoodSlots(inventory, blacklistChecker);
                return extractRandomFood(inventory, foodSlots);
            }
            return ItemStack.EMPTY;
        }

        static List<BackpackBlockInfo> findNearbyBackpackBlocksWithFood(Level level, BlockPos center, Predicate<ItemStack> blacklistChecker) {
            List<BackpackBlockInfo> result = new ArrayList<>();
            int range = EvilSeagullConfig.PLACED_BACKPACK_SEARCH_RANGE.get();

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos checkPos = center.offset(x, y, z);
                        try {
                            if (level.getBlockState(checkPos).getBlock() instanceof BackpackBlock) {
                                BlockEntity be = level.getBlockEntity(checkPos);
                                if (be instanceof BackpackBlockEntity backpackBE) {
                                    if (hasFoodInBackpackBlock(backpackBE, blacklistChecker)) {
                                        result.add(new BackpackBlockInfo(checkPos));
                                    }
                                }
                            }
                        } catch (Throwable e) {
                        }
                    }
                }
            }
            return result;
        }

        static boolean hasFoodInBackpackBlock(BackpackBlockEntity backpackBE, Predicate<ItemStack> blacklistChecker) {
            try {
                IStorageWrapper wrapper = backpackBE.getBackpackWrapper();
                IItemHandler inventory = wrapper.getInventoryHandler();
                return hasFood(inventory, blacklistChecker);
            } catch (Throwable e) {
                return false;
            }
        }

        static ItemStack extractFoodFromBackpackBlock(Level level, BlockPos pos, Predicate<ItemStack> blacklistChecker) {
            try {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BackpackBlockEntity backpackBE) {
                    IStorageWrapper wrapper = backpackBE.getBackpackWrapper();
                    IItemHandler inventory = wrapper.getInventoryHandler();
                    List<Integer> foodSlots = findFoodSlots(inventory, blacklistChecker);
                    return extractRandomFood(inventory, foodSlots);
                }
            } catch (Throwable e) {
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
    }
}
