package com.lirxowo.evilseagull.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.lirxowo.evilseagull.Evilseagull;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class CosmicRenderType {

    public static final RenderType COSMIC_ENTITY = RenderType.create(
            Evilseagull.MODID + ":cosmic_entity",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> CosmicShaders.COSMIC_ENTITY_SHADER))
                    .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(true)
    );
}
