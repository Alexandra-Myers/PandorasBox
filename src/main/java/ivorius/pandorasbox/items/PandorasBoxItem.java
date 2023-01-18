/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.items;

import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;

public class PandorasBoxItem extends BlockItem
{
    public PandorasBoxItem(Block block, Item.Properties properties)
    {
        super(block, properties);
    }

    @Override
    public ActionResultType place(BlockItemUseContext context) {
        ActionResultType result = super.place(context);
        BlockItemUseContext blockitemusecontext = this.updatePlacementContext(context);
        if (blockitemusecontext != null) {
            executeRandomEffect(context.getLevel(), context.getPlayer());
            context.getLevel().removeBlock(blockitemusecontext.getClickedPos(), false);
            context.getLevel().removeBlockEntity(blockitemusecontext.getClickedPos());
        }

        return result;
    }

    public static PandorasBoxEntity executeRandomEffect(World world, Entity entity)
    {
//        if(PBECRegistry.isAnyNull(world, entity)) return null;
        if(world.isClientSide()) return null;
        return PBECRegistry.spawnPandorasBox(world, world.random, true, entity);
    }
}
