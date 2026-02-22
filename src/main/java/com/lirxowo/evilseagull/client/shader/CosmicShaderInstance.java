package com.lirxowo.evilseagull.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CosmicShaderInstance extends ShaderInstance {

    private final List<Runnable> applyCallbacks = new ArrayList<>();

    public CosmicShaderInstance(ResourceProvider resourceProvider, ResourceLocation location, VertexFormat format) throws IOException {
        super(resourceProvider, location, format);
    }

    public void onApply(Runnable callback) {
        applyCallbacks.add(callback);
    }

    @Override
    public void apply() {
        for (Runnable callback : applyCallbacks) {
            callback.run();
        }
        super.apply();
    }
}
