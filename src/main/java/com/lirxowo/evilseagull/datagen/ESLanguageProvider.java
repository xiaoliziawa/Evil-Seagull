package com.lirxowo.evilseagull.datagen;

import com.lirxowo.evilseagull.Evilseagull;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ESLanguageProvider extends LanguageProvider {

    private final String locale;

    public ESLanguageProvider(PackOutput output, String locale) {
        super(output, Evilseagull.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if (locale.equals("en_us")) {
            add("advancement.evilseagull.seagull_steal_baked_potato.title", "French Fries at the Pier");
            add("advancement.evilseagull.seagull_steal_baked_potato.description", "Witness a seagull stealing baked potatoes from a Sophisticated Backpack or ME Interface");
        } else if (locale.equals("zh_cn")) {
            add("advancement.evilseagull.seagull_steal_baked_potato.title", "去码头整点薯条");
            add("advancement.evilseagull.seagull_steal_baked_potato.description", "目睹海鸥从精妙背包或ME接口中偷走烤马铃薯");
        }
    }
}
