package com.lirxowo.evilseagull;

import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Evilseagull.MODID)
public class Evilseagull {

    public static final String MODID = "evilseagull";

    public Evilseagull() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EvilSeagullConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
