package com.lirxowo.evilseagull.datagen;

import com.lirxowo.evilseagull.Evilseagull;
import com.lirxowo.evilseagull.advancement.ESAdvancementTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ESAdvancementProvider extends ForgeAdvancementProvider {

    public ESAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ESAdvancementGenerator()));
    }

    public static class ESAdvancementGenerator implements AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
            Advancement.Builder.advancement()
                    .display(
                            Items.BAKED_POTATO,
                            Component.translatable("advancement.evilseagull.seagull_steal_baked_potato.title"),
                            Component.translatable("advancement.evilseagull.seagull_steal_baked_potato.description"),
                            new ResourceLocation("minecraft:textures/block/farmland_moist.png"),
                            FrameType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("seagull_steal_baked_potato",
                            new ESAdvancementTrigger.Instance(
                                    ContextAwarePredicate.ANY,
                                    new ResourceLocation(Evilseagull.MODID, "seagull_steal_baked_potato")
                            ))
                    .rewards(AdvancementRewards.Builder.experience(10))
                    .save(saver, new ResourceLocation(Evilseagull.MODID, "seagull_steal_baked_potato"), existingFileHelper);
        }
    }
}
