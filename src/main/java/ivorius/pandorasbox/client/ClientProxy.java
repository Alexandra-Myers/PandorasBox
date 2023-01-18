/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.client;

import ivorius.pandorasbox.PBProxy;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PBBlocks;
import ivorius.pandorasbox.client.rendering.RenderPandorasBox;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRendererExplosion;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements PBProxy
{
    @Override
    public void preInit()
    {

        PBEffectRenderingRegistry.registerRenderer(PBEffectExplode.class, new PBEffectRendererExplosion());

    }

    @Override
    public void load()
    {
    }

    @Override
    public void loadConfig(String categoryID)
    {

    }
}
