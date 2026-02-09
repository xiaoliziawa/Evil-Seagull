package com.lirxowo.evilseagull.datagen;

import com.lirxowo.evilseagull.Evilseagull;
import com.lirxowo.evilseagull.advancement.ESAdvancementTrigger;
import com.lirxowo.evilseagull.advancement.ESAdvancementTriggerRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ESAdvancementProvider extends AdvancementProvider {

    public ESAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ESAdvancementGenerator()));
    }

    public static class ESAdvancementGenerator implements AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            Advancement.Builder.advancement()
                    .display(
                            Items.BAKED_POTATO,
                            Component.translatable("advancement.evilseagull.seagull_steal_baked_potato.title"),
                            Component.translatable("advancement.evilseagull.seagull_steal_baked_potato.description"),
                            ResourceLocation.withDefaultNamespace("textures/block/farmland_moist.png"),
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("seagull_steal_baked_potato",
                            new Criterion<>(ESAdvancementTriggerRegistry.SEAGULL_STEAL_BAKED_POTATO.get(),
                                    ESAdvancementTrigger.TriggerInstance.create()))
                    .rewards(AdvancementRewards.Builder.experience(10))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Evilseagull.MODID, "seagull_steal_baked_potato"), existingFileHelper);
        }
    }
}
