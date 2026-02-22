package com.lirxowo.evilseagull;

import com.lirxowo.evilseagull.advancement.ESAdvancementTriggerRegistry;
import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Evilseagull.MODID)
public class Evilseagull {

    public static final String MODID = "evilseagull";

    public Evilseagull(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, EvilSeagullConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, EvilSeagullConfig.CLIENT_SPEC);
        ESAdvancementTriggerRegistry.TRIGGER_TYPES.register(modEventBus);
    }
}
