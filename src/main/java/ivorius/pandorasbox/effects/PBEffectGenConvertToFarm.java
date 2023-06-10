/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToFarm extends PBEffectGenerate
{
    private double cropChance;
    public PBEffectGenConvertToFarm() {}

    public PBEffectGenConvertToFarm(int time, double range, int unifiedSeed, double cropChance)
    {
        super(time, range, 2, unifiedSeed);
        this.cropChance = cropChance;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerLevel)
        {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0)
            {
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);

                if (blockBelowState.isRedstoneConductor(world, pos) && blockState.isAir() && block != Blocks.WATER)
                {
                    if (random.nextDouble() < cropChance)
                    {
                        int b = world.random.nextInt(7);

                        if (b == 0)
                        {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.PUMPKIN_STEM.getStateDefinition().getPossibleStates().get(world.random.nextInt(4) + 4));
                        }
                        else if (b == 1)
                        {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.MELON_STEM.getStateDefinition().getPossibleStates().get(world.random.nextInt(4) + 4));
                        }
                        else if (b == 2)
                        {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.WHEAT.getStateDefinition().getPossibleStates().get(world.random.nextInt(8)));
                        }
                        else if (b == 3)
                        {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.CARROTS.getStateDefinition().getPossibleStates().get(world.random.nextInt(8)));
                        }
                        else if (b == 4)
                        {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.POTATOES.getStateDefinition().getPossibleStates().get(world.random.nextInt(8)));
                        }
                        else if (b == 5)
                        {
                            setBlockSafe(world, pos, Blocks.HAY_BLOCK.defaultBlockState());
                        }
                        else {
                            setBlockSafe(world, posBelow, Blocks.WATER.defaultBlockState());
                        }
                    }
                }
            }
            else
            {
                ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilySpawnEntity(world, entity, random, "horse", 1.0f / (50 * 50), pos),
                lazilySpawnEntity(world, entity, random, "villager", 1.0f / (50 * 50), pos),
                lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (50 * 50), pos),
                lazilySpawnEntity(world, entity, random, "pig", 1.0f / (50 * 50), pos),
                lazilySpawnEntity(world, entity, random, "cow", 1.0f / (50 * 50), pos),
                lazilySpawnEntity(world, entity, random, "chicken", 1.0f / (50 * 50), pos));
                for(Entity entity1 : entities) {
                    canSpawnEntity(world, blockState, pos, entity1);
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putDouble("cropChance", cropChance);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        cropChance = compound.getDouble("cropChance");
    }
}
