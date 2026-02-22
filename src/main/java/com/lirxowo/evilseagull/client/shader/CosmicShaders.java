package com.lirxowo.evilseagull.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.lirxowo.evilseagull.Evilseagull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import java.io.IOException;
import java.util.Objects;

public class CosmicShaders {

    public static CosmicShaderInstance COSMIC_ENTITY_SHADER;

    public static Uniform cosmicTime;
    public static Uniform cosmicYaw;
    public static Uniform cosmicPitch;
    public static Uniform cosmicExternalScale;
    public static Uniform cosmicOpacity;
    public static Uniform cosmicUVs;

    public static final float[] COSMIC_UV_DATA = new float[40];

    public static int renderTick;
    public static float renderFrame;

    @EventBusSubscriber(modid = Evilseagull.MODID, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) {
            try {
                CosmicShaderInstance shader = new CosmicShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(Evilseagull.MODID, "cosmic_entity"),
                        DefaultVertexFormat.NEW_ENTITY
                );
                event.registerShader(shader, CosmicShaders::onCosmicShaderLoaded);
            } catch (IOException e) {
                throw new RuntimeException("Failed to register cosmic entity shader", e);
            }
        }
    }

    private static void onCosmicShaderLoaded(ShaderInstance shader) {
        COSMIC_ENTITY_SHADER = (CosmicShaderInstance) shader;
        cosmicTime = Objects.requireNonNull(COSMIC_ENTITY_SHADER.getUniform("time"));
        cosmicYaw = Objects.requireNonNull(COSMIC_ENTITY_SHADER.getUniform("yaw"));
        cosmicPitch = Objects.requireNonNull(COSMIC_ENTITY_SHADER.getUniform("pitch"));
        cosmicExternalScale = Objects.requireNonNull(COSMIC_ENTITY_SHADER.getUniform("externalScale"));
        cosmicOpacity = Objects.requireNonNull(COSMIC_ENTITY_SHADER.getUniform("opacity"));
        cosmicUVs = Objects.requireNonNull(COSMIC_ENTITY_SHADER.getUniform("cosmicuvs"));

        cosmicTime.set((float) renderTick + renderFrame);
        COSMIC_ENTITY_SHADER.onApply(() -> {
            cosmicTime.set((float) renderTick + renderFrame);
        });
    }

    public static void updateCosmicUVs() {
        Minecraft mc = Minecraft.getInstance();
        for (int i = 0; i < 10; i++) {
            TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(ResourceLocation.fromNamespaceAndPath("renderblender", "misc/cosmic_" + i));
            COSMIC_UV_DATA[i * 4] = sprite.getU0();
            COSMIC_UV_DATA[i * 4 + 1] = sprite.getV0();
            COSMIC_UV_DATA[i * 4 + 2] = sprite.getU1();
            COSMIC_UV_DATA[i * 4 + 3] = sprite.getV1();
        }
        if (cosmicUVs != null) {
            cosmicUVs.set(COSMIC_UV_DATA);
        }
    }

    @EventBusSubscriber(modid = Evilseagull.MODID, value = Dist.CLIENT)
    public static class GameEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (!Minecraft.getInstance().isPaused()) {
                renderTick++;
            }
        }

        @SubscribeEvent
        public static void onRenderFrame(RenderFrameEvent.Pre event) {
            if (!Minecraft.getInstance().isPaused()) {
                renderFrame = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            }
        }
    }
}
