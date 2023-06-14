/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.worldgen.MegaTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

                    T generator = getRandomGenerator(getGenerators(), generatorFlags, random);
                    if (generator instanceof ResourceKey<?> key) {
                        Optional<Registry<ConfiguredFeature<?, ?>>> configuredFeatureRegistry = world.registryAccess().registry(Registries.CONFIGURED_FEATURE);
                        if(configuredFeatureRegistry.isEmpty()) return;
                        ConfiguredFeature<?, ?> feature = configuredFeatureRegistry.get().get((ResourceKey<ConfiguredFeature<?, ?>>) key);
                        assert feature != null;
                        feature.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                    }
                    if(generator instanceof MegaTreeFeature feature) {
                        feature.place(world, random, pos);
                    }
                }
            }
        }
    }

    public abstract List<T> getGenerators();

    public T getRandomGenerator(List<T> generators, int flags, RandomSource random)
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
