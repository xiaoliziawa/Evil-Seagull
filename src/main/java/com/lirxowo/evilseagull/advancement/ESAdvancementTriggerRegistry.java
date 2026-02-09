package com.lirxowo.evilseagull.advancement;

import com.lirxowo.evilseagull.Evilseagull;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ESAdvancementTriggerRegistry {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Evilseagull.MODID);

    public static final Supplier<ESAdvancementTrigger> SEAGULL_STEAL_BAKED_POTATO =
            TRIGGER_TYPES.register("seagull_steal_baked_potato", ESAdvancementTrigger::new);

    public static void init() {
    }
}
