package ivorius.pandorasbox.mixin.client;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.client.rendering.PandorasBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelRenderers.class)
public class SpecialModelRenderersMixin {
    @Shadow
    @Final
    public static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void injectBoxRenderer(CallbackInfo ci) {
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("pandoras_box"), PandorasBoxSpecialRenderer.Unbaked.MAP_CODEC);
    }
}