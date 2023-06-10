/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.worldgen.MegaTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerateByGenerator extends PBEffectGenerate
{
    public boolean requiresSolidGround;
    public double chancePerBlock;

    public int generatorFlags;
    public PBEffectGenerateByGenerator() {}

    public PBEffectGenerateByGenerator(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags)
    {
        super(time, range, 1, unifiedSeed);

        this.requiresSolidGround = requiresSolidGround;
        this.chancePerBlock = chancePerBlock;
        this.generatorFlags = generatorFlags;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerLevel serverWorld)
        {
            if (random.nextDouble() < chancePerBlock)
            {
                BlockState blockState = world.getBlockState(pos);
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);

                if (blockState.isAir() && (!requiresSolidGround || blockBelowState.isRedstoneConductor(world, posBelow)))
                {
                    setBlockSafe(world, posBelow, Blocks.DIRT.defaultBlockState());

                    Object generator = getRandomGenerator(getGenerators(), generatorFlags, random);
                    if(generator instanceof ConfiguredFeature) {
                        ConfiguredFeature<?, ?> feature = (ConfiguredFeature<?, ?>) generator;
                        feature.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                    }
                    if(generator instanceof MegaTreeFeature) {
                        MegaTreeFeature feature = (MegaTreeFeature) generator;
                        feature.place(world, random, pos);
                    }
                }
            }
        }
    }

    public abstract Object[] getGenerators();

    public static Object getRandomGenerator(Object[] generators, int flags, RandomSource random)
    {
        int totalNumber = 0;

        for (int i = 0; i < generators.length; i++)
        {
            int flag = 1 << i;
            if ((flags & flag) > 0)
            {
                totalNumber++;
            }
        }

        int chosenGen = random.nextInt(totalNumber);
        for (int i = 0; i < generators.length; i++)
        {
            int flag = 1 << i;
            if ((flags & flag) > 0)
            {
                if (chosenGen == 0)
                {
                    return generators[i];
                }

                chosenGen--;
            }
        }

        return null;
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putBoolean("requiresSolidGround", requiresSolidGround);
        compound.putDouble("chancePerBlock", chancePerBlock);
        compound.putInt("generatorFlags", generatorFlags);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        requiresSolidGround = compound.getBoolean("requiresSolidGround");
        chancePerBlock = compound.getDouble("chancePerBlock");
        generatorFlags = compound.getInt("generatorFlags");
    }
}
