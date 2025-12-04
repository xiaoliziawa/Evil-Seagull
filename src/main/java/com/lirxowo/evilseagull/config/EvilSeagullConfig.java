package com.lirxowo.evilseagull.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class EvilSeagullConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_SOPHISTICATED_BACKPACKS;
    public static final ForgeConfigSpec.IntValue SOPHISTICATED_BACKPACK_SEARCH_RANGE;
    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_PLACED_BACKPACKS;
    public static final ForgeConfigSpec.IntValue PLACED_BACKPACK_SEARCH_RANGE;
    public static final ForgeConfigSpec.BooleanValue BACKPACK_STEAL_ANY_ITEM;

    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_ME_INTERFACE;
    public static final ForgeConfigSpec.IntValue ME_INTERFACE_SEARCH_RANGE;
    public static final ForgeConfigSpec.DoubleValue ME_POWER_PER_STEAL;
    public static final ForgeConfigSpec.BooleanValue ME_STEAL_ANY_ITEM;

    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_RS_INTERFACE;
    public static final ForgeConfigSpec.IntValue RS_INTERFACE_SEARCH_RANGE;
    public static final ForgeConfigSpec.IntValue RS_ENERGY_PER_STEAL;
    public static final ForgeConfigSpec.BooleanValue RS_STEAL_ANY_ITEM;

    public static final ForgeConfigSpec.BooleanValue STEAL_FROM_CREATE_BELT;
    public static final ForgeConfigSpec.IntValue CREATE_BELT_SEARCH_RANGE;
    public static final ForgeConfigSpec.BooleanValue CREATE_BELT_STEAL_ANY_ITEM;
    public static final ForgeConfigSpec.IntValue CREATE_BELT_DROP_RANGE_MIN;
    public static final ForgeConfigSpec.IntValue CREATE_BELT_DROP_RANGE_MAX;
    public static final ForgeConfigSpec.IntValue CREATE_BELT_HOVER_TIME_MIN;
    public static final ForgeConfigSpec.IntValue CREATE_BELT_HOVER_TIME_MAX;

    public static final ForgeConfigSpec.IntValue STEAL_COOLDOWN_MODIFIER;
    public static final ForgeConfigSpec.BooleanValue PRIORITIZE_PLAYER_INVENTORY;
    public static final ForgeConfigSpec.IntValue DROP_RANGE_MIN;
    public static final ForgeConfigSpec.IntValue DROP_RANGE_MAX;

    static {
        BUILDER.push("sophisticated_backpacks");

        STEAL_FROM_SOPHISTICATED_BACKPACKS = BUILDER
                .define("enableStealFromBackpacks", true);

        SOPHISTICATED_BACKPACK_SEARCH_RANGE = BUILDER
                .defineInRange("backpackSearchRange", 10, 1, 50);

        STEAL_FROM_PLACED_BACKPACKS = BUILDER
                .define("enableStealFromPlacedBackpacks", true);

        PLACED_BACKPACK_SEARCH_RANGE = BUILDER
                .defineInRange("placedBackpackSearchRange", 16, 1, 32);

        BACKPACK_STEAL_ANY_ITEM = BUILDER
                .define("stealAnyItem", false);

        BUILDER.pop();
        BUILDER.push("applied_energistics");

        STEAL_FROM_ME_INTERFACE = BUILDER
                .define("enableStealFromMEInterface", true);

        ME_INTERFACE_SEARCH_RANGE = BUILDER
                .defineInRange("meInterfaceSearchRange", 16, 1, 32);

        ME_POWER_PER_STEAL = BUILDER
                .defineInRange("powerPerSteal", 10.0, 0.0, 1000.0);

        ME_STEAL_ANY_ITEM = BUILDER
                .define("stealAnyItem", false);

        BUILDER.pop();
        BUILDER.push("refined_storage");

        STEAL_FROM_RS_INTERFACE = BUILDER
                .define("enableStealFromRSInterface", true);

        RS_INTERFACE_SEARCH_RANGE = BUILDER
                .defineInRange("rsInterfaceSearchRange", 16, 1, 32);

        RS_ENERGY_PER_STEAL = BUILDER
                .defineInRange("energyPerSteal", 10, 0, 1000);

        RS_STEAL_ANY_ITEM = BUILDER
                .define("stealAnyItem", false);

        BUILDER.pop();
        BUILDER.push("create");

        STEAL_FROM_CREATE_BELT = BUILDER
                .define("enableStealFromBelt", true);

        CREATE_BELT_SEARCH_RANGE = BUILDER
                .defineInRange("beltSearchRange", 16, 1, 32);

        CREATE_BELT_STEAL_ANY_ITEM = BUILDER
                .define("stealAnyItem", true);

        CREATE_BELT_DROP_RANGE_MIN = BUILDER
                .defineInRange("dropRangeMin", 5, 1, 50);

        CREATE_BELT_DROP_RANGE_MAX = BUILDER
                .defineInRange("dropRangeMax", 15, 1, 100);

        CREATE_BELT_HOVER_TIME_MIN = BUILDER
                .defineInRange("hoverTimeMin", 40, 0, 200);

        CREATE_BELT_HOVER_TIME_MAX = BUILDER
                .defineInRange("hoverTimeMax", 80, 0, 400);

        BUILDER.pop();
        BUILDER.push("general");

        STEAL_COOLDOWN_MODIFIER = BUILDER
                .defineInRange("stealCooldownModifier", 100, 50, 500);

        PRIORITIZE_PLAYER_INVENTORY = BUILDER
                .define("prioritizePlayerInventory", true);

        DROP_RANGE_MIN = BUILDER
                .defineInRange("dropRangeMin", 5, 1, 50);

        DROP_RANGE_MAX = BUILDER
                .defineInRange("dropRangeMax", 15, 1, 100);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
