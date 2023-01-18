/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHalloween extends PBEffectGenerate
{
    public PBEffectGenConvertToHalloween(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, EntityPandorasBox entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerWorld)
        {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0)
            {
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);
                Block blockBelow = blockBelowState.getBlock();

                if (blockBelow.getBlockSupportShape(blockBelowState, world, posBelow) == Blocks.GRASS_BLOCK.getBlockSupportShape(Blocks.GRASS_BLOCK.defaultBlockState(), world, pos) && blockState.isAir(world, pos) && block != Blocks.WATER)
                {
                    if (random.nextInt(5 * 5) == 0)
                    {
                        int b = world.random.nextInt(6);

                        if (b == 0)
                        {
                            setBlockSafe(world, posBelow, Blocks.NETHERRACK.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                        }
                        else if (b == 1)
                        {
                            setBlockSafe(world, pos, Blocks.JACK_O_LANTERN.defaultBlockState());
                        }
                        else if (b == 2)
                        {
                            setBlockSafe(world, pos, Blocks.PUMPKIN.defaultBlockState());
                        }
                        else if (b == 3)
                        {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.PUMPKIN_STEM.getStateDefinition().getPossibleStates().get(world.random.nextInt(4) + 4));
                        }
                        else if (b == 4)
                        {
                            setBlockSafe(world, pos, Blocks.CAKE.defaultBlockState());
                        }
                        else if (b == 5)
                        {
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            world.addFreshEntity(entityItem);
                        }
                    }
                }
            }
            else
            {
                ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilySpawnEntity(world, entity, random, "zombie_pigman", 1.0f / (20 * 20), pos),
                lazilySpawnEntity(world, entity, random, "enderman", 1.0f / (20 * 20), pos));
                for (Entity entity1 : entities) {
                    canSpawnEntity(world, blockState, pos, entity1);
                }
            }
        }
    }
}
