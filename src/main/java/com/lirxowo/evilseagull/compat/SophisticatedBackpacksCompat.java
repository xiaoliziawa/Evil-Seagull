package com.lirxowo.evilseagull.compat;

import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
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

        static boolean hasFoodInBackpack(ItemStack backpackStack, Predicate<ItemStack> blacklistChecker) {
            try {
                Optional<IStorageWrapper> wrapperOpt = backpackStack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).resolve().map(w -> (IStorageWrapper) w);

                if (wrapperOpt.isPresent()) {
                    IStorageWrapper wrapper = wrapperOpt.get();
                    IItemHandler inventory = wrapper.getInventoryHandler();

                    for (int slot = 0; slot < inventory.getSlots(); slot++) {
                        ItemStack slotStack = inventory.getStackInSlot(slot);
                        if (!slotStack.isEmpty() && slotStack.isEdible() && !blacklistChecker.test(slotStack)) {
                            return true;
                        }
                    }
                }
            } catch (Throwable e) {
            }
            return false;
        }

        static ItemStack extractFoodFromBackpack(ItemStack backpackStack, Predicate<ItemStack> blacklistChecker) {
            try {
                Optional<IStorageWrapper> wrapperOpt = backpackStack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).resolve().map(w -> (IStorageWrapper) w);

                if (wrapperOpt.isPresent()) {
                    IStorageWrapper wrapper = wrapperOpt.get();
                    IItemHandler inventory = wrapper.getInventoryHandler();

                    List<Integer> foodSlots = new ArrayList<>();
                    for (int slot = 0; slot < inventory.getSlots(); slot++) {
                        ItemStack slotStack = inventory.getStackInSlot(slot);
                        if (!slotStack.isEmpty() && slotStack.isEdible() && !blacklistChecker.test(slotStack)) {
                            foodSlots.add(slot);
                        }
                    }

                    if (!foodSlots.isEmpty()) {
                        int randomIndex = foodSlots.size() <= 1 ? 0 : (int) (Math.random() * foodSlots.size());
                        int selectedSlot = foodSlots.get(randomIndex);
                        return inventory.extractItem(selectedSlot, 1, false);
                    }
                }
            } catch (Throwable e) {
            }
            return ItemStack.EMPTY;
        }
    }
}
