package com.lirxowo.evilseagull.client.render;

import com.lirxowo.evilseagull.client.model.ChristmasHatModel;
import com.lirxowo.evilseagull.client.model.ChristmasHatModel.ChristmasHatAnimatable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

public class ChristmasHatRenderer implements GeoRenderer<ChristmasHatAnimatable> {

    protected final GeoRenderLayersContainer<ChristmasHatAnimatable> renderLayers = new GeoRenderLayersContainer<>(this);
    protected final ChristmasHatModel model;
    protected ChristmasHatAnimatable animatable;

    protected Matrix4f objectRenderTranslations = new Matrix4f();
    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public ChristmasHatRenderer() {
        this.model = new ChristmasHatModel();
    }

    @Override
    public GeoModel<ChristmasHatAnimatable> getGeoModel() {
        return this.model;
    }

    @Override
    public ChristmasHatAnimatable getAnimatable() {
        return this.animatable;
    }

    @Override
    public ResourceLocation getTextureLocation(ChristmasHatAnimatable animatable) {
        return this.model.getTextureResource(animatable);
    }

    @Override
    public List<GeoRenderLayer<ChristmasHatAnimatable>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    @Override
    public void updateAnimatedTextureFrame(ChristmasHatAnimatable animatable) {
        AnimatableTexture.setAndUpdate(getTextureLocation(animatable));
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.animatable = ChristmasHatAnimatable.INSTANCE;

        poseStack.pushPose();

        RenderType renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, 0);
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        BakedGeoModel bakedModel = this.model.getBakedModel(this.model.getModelResource(animatable));

        this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());

        float scale = 0.0625F;
        poseStack.scale(scale, scale, scale);

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        int packedOverlay = getPackedOverlay(animatable, 0, 0);
        int colour = Color.WHITE.argbInt();
        actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, buffer,
            false, 0, packedLight, packedOverlay, colour);

        poseStack.popPose();

        this.animatable = null;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ChristmasHatAnimatable animatable, BakedGeoModel model,
                               RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                               int colour) {
        poseStack.pushPose();

        if (!isReRender) {
            AnimationState<ChristmasHatAnimatable> animationState = new AnimationState<>(animatable, 0, 0, partialTick, false);
            long instanceId = getInstanceId(animatable);

            this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);
            this.model.handleAnimations(animatable, instanceId, animationState, partialTick);
        }

        GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer,
            isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ChristmasHatAnimatable animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  int colour) {
        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.objectRenderTranslations));
        }

        GeoRenderer.super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
            isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void fireCompileRenderLayersEvent() {
    }

    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource,
                                      float partialTick, int packedLight) {
        return true;
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource,
                                    float partialTick, int packedLight) {
    }
}
