package com.lirxowo.evilseagull.advancement;

import com.lirxowo.evilseagull.Evilseagull;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class ESAdvancementTriggerRegistry {

    public static final ESAdvancementTrigger SEAGULL_STEAL_BAKED_POTATO = new ESAdvancementTrigger(
            new ResourceLocation(Evilseagull.MODID, "seagull_steal_baked_potato")
    );

    public static void init() {
        CriteriaTriggers.register(SEAGULL_STEAL_BAKED_POTATO);
    }
}
