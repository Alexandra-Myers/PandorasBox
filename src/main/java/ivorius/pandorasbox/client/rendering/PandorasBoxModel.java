/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

// Date: 18.04.2014 00:26:40
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX


package ivorius.pandorasbox.client.rendering;

import com.google.common.collect.ImmutableList;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.projectile.ArrowEntity;

public class PandorasBoxModel extends SegmentedModel<ArrowEntity> {
    private final ModelRenderer feet;
    private final ModelRenderer body;
    private final ModelRenderer joint;
    private final ModelRenderer top;
    public PandorasBoxEntity PBE;

    public PandorasBoxModel() {
        texWidth = 32;
        texHeight = 32;

        feet = new ModelRenderer(this);
        feet.setPos(0.0F, 1.0F, 0.0F);
        feet.texOffs(0, 14).addBox(-3.5F, 22.0F, 2.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        feet.texOffs(4, 14).addBox(2.5F, 22.0F, 2.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        feet.texOffs(4, 16).addBox(2.5F, 22.0F, -3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        feet.texOffs(0, 16).addBox(-3.5F, 22.0F, -3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 24.0F, 0.0F);
        body.texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 6.0F, 8.0F, 0.0F, false);

        joint = new ModelRenderer(this);
        joint.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -10.0F, -4.0F);
        joint.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.3054F, 0.0F, 0.0F);
        cube_r1.texOffs(8, 14).addBox(-3.5F, 1.75F, 0.5F, 7.0F, 1.0F, 1.0F, 0.0F, false);

        top = new ModelRenderer(this);
        top.setPos(0.0F, 16.0F, -4.0F);
        setRotationAngle(top, -0.0802F, 0.0F, 0.0F);
        top.texOffs(0, 18).addBox(-4.0F, -0.5F, 0.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);
        top.texOffs(0, 27).addBox(-2.0F, -1.5F, 2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(2.0F, -1.0F, 3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(-3.0F, -1.0F, 3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(-0.5F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(12, 16).addBox(3.25F, -0.75F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(16, 16).addBox(-4.25F, -0.75F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(ArrowEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
        top.xRot = entityIn.xRot;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(feet, body, joint, top);
    }
}