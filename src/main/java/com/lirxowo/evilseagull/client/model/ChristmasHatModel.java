package com.lirxowo.evilseagull.client.model;

import com.lirxowo.evilseagull.Evilseagull;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChristmasHatModel extends GeoModel<ChristmasHatModel.ChristmasHatAnimatable> {

    private static final ResourceLocation MODEL =
        new ResourceLocation(Evilseagull.MODID, "geo/christmas_hat.geo.json");
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(Evilseagull.MODID, "textures/entity/christmas_hat.png");

    @Override
    public ResourceLocation getModelResource(ChristmasHatAnimatable animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ChristmasHatAnimatable animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ChristmasHatAnimatable animatable) {
        return null;
    }

    public static class ChristmasHatAnimatable implements GeoAnimatable {

        public static final ChristmasHatAnimatable INSTANCE = new ChristmasHatAnimatable();
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        private ChristmasHatAnimatable() {}

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }

        @Override
        public double getTick(Object object) {
            return 0;
        }
    }
}
