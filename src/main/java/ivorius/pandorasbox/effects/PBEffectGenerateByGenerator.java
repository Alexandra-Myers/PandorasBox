/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.worldgen.AccessibleTreeFeature;
import ivorius.pandorasbox.worldgen.MegaTreeFeature;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerateByGenerator<T> extends PBEffectGenerate
{
    public boolean requiresSolidGround;
    public double chancePerBlock;

    public int generatorFlags;
    protected final ArrayListExtensions<T> treeGens;
    public PBEffectGenerateByGenerator() {
        this.treeGens = initializeGens();
    }

    public PBEffectGenerateByGenerator(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags)
    {
        super(time, range, 1, unifiedSeed);
        this.treeGens = initializeGens();

        this.requiresSolidGround = requiresSolidGround;
        this.chancePerBlock = chancePerBlock;
        this.generatorFlags = generatorFlags;
    }

    abstract ArrayListExtensions<T> initializeGens();

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerWorld)
        {
            if (random.nextDouble() < chancePerBlock)
            {
                BlockState blockState = world.getBlockState(pos);
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);

                if (blockState.getMaterial() == Material.AIR && (!requiresSolidGround || blockBelowState.isRedstoneConductor(world, posBelow)))
                {
                    setBlockSafe(world, posBelow, Blocks.DIRT.defaultBlockState());

                    T generator = getRandomGenerator(getGenerators(), generatorFlags, random);
                    if(generator instanceof ConfiguredFeature) {
                        ConfiguredFeature<?, ?> feature = (ConfiguredFeature<?, ?>) generator;
                        ServerWorld serverWorld = (ServerWorld) world;
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

    public abstract List<T> getGenerators();

    public T getRandomGenerator(List<T> generators, int flags, Random random)
    {
        int totalNumber = 0;

        for (int i = 0; i < generators.size(); i++)
        {
            int flag = 1 << i;
            if ((flags & flag) > 0)
            {
                totalNumber++;
            }
        }

        int chosenGen = random.nextInt(totalNumber);
        for (int i = 0; i < generators.size(); i++)
        {
            int flag = 1 << i;
            if ((flags & flag) > 0)
            {
                if (chosenGen == 0)
                {
                    return generators.get(i);
                }

                chosenGen--;
            }
        }

        return null;
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putBoolean("requiresSolidGround", requiresSolidGround);
        compound.putDouble("chancePerBlock", chancePerBlock);
        compound.putInt("generatorFlags", generatorFlags);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        requiresSolidGround = compound.getBoolean("requiresSolidGround");
        chancePerBlock = compound.getDouble("chancePerBlock");
        generatorFlags = compound.getInt("generatorFlags");
    }
}
