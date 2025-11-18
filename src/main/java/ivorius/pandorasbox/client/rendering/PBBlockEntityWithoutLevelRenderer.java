package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.init.ItemInit;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.*;

public class PBBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    private PandorasBoxModel boxModel;
    private final EntityModelSet entityModelSet;
    public PBBlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
        this.entityModelSet = entityModelSet;
        this.boxModel = new PandorasBoxModel(this.entityModelSet.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }

    @Override
    public void onResourceManagerReload(ResourceManager arg) {
        super.onResourceManagerReload(arg);
        this.boxModel = new PandorasBoxModel(this.entityModelSet.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if (stack.is(ItemInit.PBI.get())) {
            PandorasBoxBlockEntityRenderer.renderItem(poseStack, multiBufferSource, this.boxModel, i, j, stack.hasFoil());
        }
        super.renderByItem(stack, itemDisplayContext, poseStack, multiBufferSource, i, j);
    }
}