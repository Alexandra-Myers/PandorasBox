package ivorius.pandorasbox.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.init.ItemInit;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class RendererMixin {
    @Unique
    private PandorasBoxModel boxModel;
    @Final
    @Shadow
    private EntityModelSet entityModelSet;

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void setEntityModelSet(CallbackInfo ci) {
        this.boxModel = new PandorasBoxModel(this.entityModelSet.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }

    @Inject(method = "renderByItem", at = @At("HEAD"))
    private void mainRender(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci) {
        if (stack.is(ItemInit.PBI)) {
            PandorasBoxBlockEntityRenderer.renderItem(poseStack, multiBufferSource, this.boxModel, i, j, stack.hasFoil());
        }
    }
}