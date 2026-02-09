package com.lirxowo.evilseagull.mixin.client;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.client.render.RenderSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.lirxowo.evilseagull.client.render.ChristmasHatLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSeagull.class)
public abstract class RenderSeagullMixin extends MobRenderer<EntitySeagull, ModelSeagull> {

    public RenderSeagullMixin(EntityRendererProvider.Context context, ModelSeagull model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new ChristmasHatLayer((RenderSeagull)(Object)this));
    }
}
