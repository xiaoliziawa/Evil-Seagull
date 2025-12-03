package com.lirxowo.evilseagull.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class EvilSeagullConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_SOPHISTICATED_BACKPACKS;
    public static final ForgeConfigSpec.IntValue SOPHISTICATED_BACKPACK_SEARCH_RANGE;
    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_PLACED_BACKPACKS;
    public static final ForgeConfigSpec.IntValue PLACED_BACKPACK_SEARCH_RANGE;

    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_ME_INTERFACE;
    public static final ForgeConfigSpec.IntValue ME_INTERFACE_SEARCH_RANGE;
    public static final ForgeConfigSpec.DoubleValue ME_POWER_PER_STEAL;

    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_RS_INTERFACE;
    public static final ForgeConfigSpec.IntValue RS_INTERFACE_SEARCH_RANGE;
    public static final ForgeConfigSpec.IntValue RS_ENERGY_PER_STEAL;

    public static final ForgeConfigSpec.IntValue STEAL_COOLDOWN_MODIFIER;
    public static final ForgeConfigSpec.BooleanValue PRIORITIZE_PLAYER_INVENTORY;

    static {
        BUILDER.push("sophisticated_backpacks");

        STEAL_FROM_SOPHISTICATED_BACKPACKS = BUILDER
                .comment("Enable stealing from sophisticated backpacks in player inventory")
                .define("enableStealFromBackpacks", true);

        SOPHISTICATED_BACKPACK_SEARCH_RANGE = BUILDER
                .defineInRange("backpackSearchRange", 10, 1, 50);

        STEAL_FROM_PLACED_BACKPACKS = BUILDER
                .comment("Enable stealing from sophisticated backpacks placed as blocks in the world")
                .define("enableStealFromPlacedBackpacks", true);

        PLACED_BACKPACK_SEARCH_RANGE = BUILDER
                .comment("Search range for placed backpack blocks")
                .defineInRange("placedBackpackSearchRange", 8, 1, 32);

        BUILDER.pop();
        BUILDER.push("applied_energistics");

        STEAL_FROM_ME_INTERFACE = BUILDER
                .define("enableStealFromMEInterface", true);

        ME_INTERFACE_SEARCH_RANGE = BUILDER
                .defineInRange("meInterfaceSearchRange", 8, 1, 32);

        ME_POWER_PER_STEAL = BUILDER
                .defineInRange("powerPerSteal", 10.0, 0.0, 1000.0);

        BUILDER.pop();
        BUILDER.push("refined_storage");

        STEAL_FROM_RS_INTERFACE = BUILDER
                .comment("Enable stealing from Refined Storage Interface blocks")
                .define("enableStealFromRSInterface", true);

        RS_INTERFACE_SEARCH_RANGE = BUILDER
                .comment("Search range for RS Interface blocks")
                .defineInRange("rsInterfaceSearchRange", 8, 1, 32);

        RS_ENERGY_PER_STEAL = BUILDER
                .comment("Energy cost per steal from RS network (in FE)")
                .defineInRange("energyPerSteal", 10, 0, 1000);

        BUILDER.pop();
        BUILDER.push("general");

        STEAL_COOLDOWN_MODIFIER = BUILDER
                .defineInRange("stealCooldownModifier", 100, 50, 500);

        PRIORITIZE_PLAYER_INVENTORY = BUILDER
                .define("prioritizePlayerInventory", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
