/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderer;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.math.Axis.YP;

/**
 * Created by lukas on 30.03.14.
 */
@Environment(EnvType.CLIENT)
public class PandorasBoxRenderer extends EntityRenderer<PandorasBoxEntity, PandorasBoxRenderState> implements RenderLayerParent<PandorasBoxRenderState, PandorasBoxModel> {
    private final ItemModelResolver itemModelResolver;
    public PandorasBoxModel model;
    public Identifier texture = Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

    public PandorasBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);

        this.model = new PandorasBoxModel(renderManager.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
        this.shadowRadius = 0.6F;
        this.itemModelResolver = renderManager.getItemModelResolver();
    }

    @Override
    public @NotNull PandorasBoxRenderState createRenderState() {
        return new PandorasBoxRenderState();
    }

    @Override
    public void submit(PandorasBoxRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
        int packedLightIn = renderState.lightCoords;
        poseStack.pushPose();
        poseStack.mulPose(YP.rotationDegrees(-renderState.yRot));

        PandoraEffectRenderState pandoraEffectRenderState = renderState.pandoraEffectRenderState;
        float timePassed = ((pandoraEffectRenderState.effectTicksExisted + renderState.partialTicks) % pandoraEffectRenderState.maxTicksAlive) / pandoraEffectRenderState.maxTicksAlive;

        float boxScale = renderState.boxScale;
        if (boxScale < 1.0f)
            poseStack.scale(boxScale, boxScale, boxScale);
        float height = 0.0625F * Mth.sin((float) (timePassed * 4 * Math.PI));

        int packedOverlay = OverlayTexture.NO_OVERLAY;
        height += renderState.renderItem.isEmpty() ? 1 : -0.25F;
        poseStack.translate(0, 0.5F + height, 0);
        if (renderState.renderItem.isEmpty()) poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        else poseStack.mulPose(YP.rotation((float) (timePassed * 4 * Math.PI)));
        boolean visible = !renderState.isInvisible;
        boolean visibleToPlayer = !visible && !renderState.invisibleToPlayer;
        RenderType renderType = getRenderType(visible, visibleToPlayer);
        if (renderType != null) {
            if (renderState.renderItem.isEmpty()) submitNodeCollector.submitModel(model, renderState, poseStack, renderType, packedLightIn, packedOverlay, renderState.outlineColor, null);
            else renderState.renderItem.submit(poseStack, submitNodeCollector, packedLightIn, packedOverlay, renderState.outlineColor);
            if (pandoraEffectRenderState.shouldRender(renderState.boxDeathTicks)) {
                List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> layers = new ArrayList<>();
                PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
                renderer.renderBox(this, renderState, pandoraEffectRenderState, poseStack, submitNodeCollector, packedLightIn, height, timePassed);
                List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> renderLayers = renderer.getLayers(this, renderState, pandoraEffectRenderState, model);
                if (renderLayers != null) {
                    layers.addAll(renderLayers);
                }

                for (RenderLayer<PandorasBoxRenderState, PandorasBoxModel> renderLayer : layers) {
                    renderLayer.submit(poseStack, submitNodeCollector, packedLightIn, renderState, 0, 0);
                }
            }
        }

        poseStack.popPose();
    }

    @Nullable
    protected RenderType getRenderType(boolean visible, boolean visibleToPlayer) {
        Identifier identifier = this.texture;
        if (visibleToPlayer) {
            return RenderTypes.itemEntityTranslucentCull(identifier);
        } else if (visible) {
            return this.model.renderType(identifier);
        }
        return null;
    }

    @Override
    public PandorasBoxModel getModel() {
        return model;
    }

    @Override
    public void extractRenderState(PandorasBoxEntity entity, PandorasBoxRenderState entityRenderState, float partialTicks) {
        super.extractRenderState(entity, entityRenderState, partialTicks);
        entityRenderState.xRot = (float) (entity.getRatioBoxOpen(partialTicks) * 2F / 3F * Math.PI);
        entityRenderState.yRot = entity.getYRot();
        entityRenderState.boxScale = entity.getCurrentScale();
        entityRenderState.partialTicks = partialTicks;
        entityRenderState.entityTickCount = entity.tickCount;
        entityRenderState.boxDeathTicks = entity.getDeathTicks();
        this.itemModelResolver.updateForNonLiving(entityRenderState.renderItem, entity.getRenderItem(), ItemDisplayContext.GROUND, entity);
        entityRenderState.invisibleToPlayer = entity.isInvisibleTo(Minecraft.getInstance().player);
        PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForID(entity.getBoxEffect().rendererIdentifierForEffect());
        PandoraEffectRenderState renderState = renderer.createRenderState();
        renderer.extractRenderState(entityRenderState, renderState, entity.getBoxEffect(), entity.getEffectTicksExisted());
        entityRenderState.pandoraEffectRenderState = renderState;
    }
}
