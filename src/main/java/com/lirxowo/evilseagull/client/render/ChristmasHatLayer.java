package com.lirxowo.evilseagull.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.lirxowo.evilseagull.config.EvilSeagullConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.time.LocalDate;
import java.time.Month;

public class ChristmasHatLayer extends RenderLayer<EntitySeagull, ModelSeagull> {

    private final ChristmasHatRenderer hatRenderer;

    private static Boolean isChristmasSeason = null;
    private static long lastCheckTime = 0;
    private static final long CHECK_INTERVAL = 60000;

    public ChristmasHatLayer(RenderLayerParent<EntitySeagull, ModelSeagull> parent) {
        super(parent);
        this.hatRenderer = new ChristmasHatRenderer();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       EntitySeagull seagull, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!isChristmasSeason()) {
            return;
        }

        poseStack.pushPose();

        if (seagull.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0.0D, 1.5D, 0.0D);
        }

        this.getParentModel().root.translateAndRotate(poseStack);
        this.getParentModel().body.translateAndRotate(poseStack);
        this.getParentModel().head.translateAndRotate(poseStack);

        if (seagull.isBaby()) {
            poseStack.translate(0.0F, -0.375F - 0.1F, 0.0625F);
        } else {
            poseStack.translate(0.0F, -0.375F + 0.0625F, 0.0625F);
        }

        poseStack.scale(1.0F, -1.0F, 1.0F);

        if (seagull.isBaby()) {
            poseStack.scale(24.0F, 24.0F, 24.0F);
        } else {
            poseStack.scale(16.0F, 16.0F, 16.0F);
        }

        this.hatRenderer.render(poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    private static boolean isChristmasSeason() {
        try {
            if (!EvilSeagullConfig.ENABLE_CHRISTMAS_HAT.get()) {
                return false;
            }
            if (EvilSeagullConfig.FORCE_CHRISTMAS_HAT.get()) {
                return true;
            }
        } catch (Exception e) {
        }

        long currentTime = System.currentTimeMillis();

        if (isChristmasSeason == null || currentTime - lastCheckTime > CHECK_INTERVAL) {
            lastCheckTime = currentTime;
            LocalDate today = LocalDate.now();

            boolean isChristmas = today.getMonth() == Month.DECEMBER
                && today.getDayOfMonth() >= 24
                && today.getDayOfMonth() <= 26;

            isChristmasSeason = isChristmas;
        }

        return isChristmasSeason;
    }
}
