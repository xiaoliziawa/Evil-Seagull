package com.lirxowo.evilseagull.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.client.render.RenderSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.lirxowo.evilseagull.client.shader.CosmicRenderType;
import com.lirxowo.evilseagull.client.shader.CosmicShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;

public class CosmicSeagullLayer extends RenderLayer<EntitySeagull, ModelSeagull> {

    public CosmicSeagullLayer(RenderSeagull parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       EntitySeagull entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (CosmicShaders.COSMIC_ENTITY_SHADER == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        float yaw = (float) (mc.player.getYRot() * 2.0f * Math.PI / 360.0);
        float pitch = -(float) (mc.player.getXRot() * 2.0f * Math.PI / 360.0);

        CosmicShaders.cosmicYaw.set(yaw);
        CosmicShaders.cosmicPitch.set(pitch);
        CosmicShaders.cosmicExternalScale.set(1.0f);
        CosmicShaders.cosmicOpacity.set(1.0f);

        CosmicShaders.updateCosmicUVs();

        VertexConsumer buffer = bufferSource.getBuffer(CosmicRenderType.COSMIC_ENTITY);

        ResourceLocation textureLoc = this.getTextureLocation(entity);
        AbstractTexture entityTexture = mc.getTextureManager().getTexture(textureLoc);
        RenderSystem.setShaderTexture(1, entityTexture.getId());

        this.getParentModel().renderToBuffer(poseStack, buffer, packedLight,
                LivingEntityRenderer.getOverlayCoords(entity, 0.0F), -1);

        if (bufferSource instanceof MultiBufferSource.BufferSource bs) {
            bs.endBatch(CosmicRenderType.COSMIC_ENTITY);
        }
    }
}
