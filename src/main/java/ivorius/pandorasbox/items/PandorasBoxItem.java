/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.items;

import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PandorasBoxItem extends BlockItem
{
    public PandorasBoxItem(Block block, Item.Properties properties)
    {
        super(block, properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        executeRandomEffect(world, player, new BlockPos(new Vector3d(player.getX(), player.getEyeY(), player.getZ())), true);
        if (!player.abilities.instabuild) {
            itemstack.shrink(1);
        }
        return super.use(world, player, hand);
    }

    public static PandorasBoxEntity executeRandomEffect(World world, Entity entity, BlockPos pos, boolean floatAway)
    {
//        if(PBECRegistry.isAnyNull(world, entity)) return null;
        if(world.isClientSide()) return null;
        return PBECRegistry.spawnPandorasBox(world, world.random, true, entity, pos, floatAway);
    }
}
