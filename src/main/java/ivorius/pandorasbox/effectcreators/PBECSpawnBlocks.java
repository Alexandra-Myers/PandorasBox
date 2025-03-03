/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSpawnBlocks;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by lukas on 30.03.14.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record PBECSpawnBlocks(boolean shuffleBlocks, IValue number, IValue ticksPerBlock, ZValue spawnsFromEffectCenter, EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnBlocks> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.BOOL.optionalFieldOf("shuffle_blocks", true).forGetter(PBECSpawnBlocks::shuffleBlocks),
                            IValue.CODEC.fieldOf("number").forGetter(PBECSpawnBlocks::number),
                            IValue.CODEC.fieldOf("ticks_per_block").forGetter(PBECSpawnBlocks::ticksPerBlock),
                            ZValue.CODEC.fieldOf("spawns_from_effect_center").forGetter(PBECSpawnBlocks::spawnsFromEffectCenter),
                            WeightedBlock.CODEC.fieldOf("blocks").forGetter(PBECSpawnBlocks::blocks),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnBlocks::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnBlocks::valueSpawn))
                    .apply(instance, PBECSpawnBlocks::new));

    public PBECSpawnBlocks(IValue number, IValue ticksPerBlock, ZValue spawnsFromEffectCenter, EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) {
        this(true, number, ticksPerBlock, spawnsFromEffectCenter, blocks, valueThrow, valueSpawn);
    }

    public static ValueSpawn defaultShowerSpawn() {
        return new ValueSpawn(new DLinear(5.0, 30.0), new DConstant(150.0));
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int number = this.number.getValue(random);
        int ticksPerBlock = this.ticksPerBlock.getValue(random);
        Block[] blocks;
        Collection<WeightedBlock> movedBlocks = PandorasBoxHelper.assembleBlocks(this.blocks);

        if (shuffleBlocks) {
            Block[] selection = PandorasBoxHelper.getRandomBlockList(random, movedBlocks);
            blocks = constructBlocks(random, selection, number, true);
        } else {
            int max = 0;
            for (WeightedBlock weightedBlock : movedBlocks)
                max += (int) weightedBlock.weight();
            Block[] selection = new Block[max];
            max = 0;
            for (WeightedBlock weightedBlock : movedBlocks)
            {
                for (int i = 0; i < weightedBlock.weight(); i++)
                    selection[max + i] = weightedBlock.block().value();
                max += (int) weightedBlock.weight();
            }
            blocks = constructBlocks(random, selection, number, true);
        }

        return constructEffect(random, blocks, number * ticksPerBlock + 1, valueThrow.orElse(null), valueSpawn.orElse(null), this.spawnsFromEffectCenter.getValue(random));
    }

    public static Block[] constructBlocks(RandomSource random, Block[] blocks, int number, boolean mixUp) {
        ArrayList<Block> list = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            list.add(mixUp ? blocks[random.nextInt(blocks.length)] : blocks[i]);
        }

        return list.toArray(new Block[0]);
    }

    public static PBEffect constructEffect(RandomSource random, Block[] blocks, int time, ValueThrow valueThrow, ValueSpawn valueSpawn, boolean spawnsFromEffectCenter) {
        boolean canSpawn = valueSpawn != null;
        boolean canThrow = valueThrow != null;

        if (canThrow && (!canSpawn || random.nextBoolean())) {
            PBEffectSpawnBlocks effect = new PBEffectSpawnBlocks(time, blocks, !spawnsFromEffectCenter);
            PBECSpawnEntities.setEffectThrow(effect, random, valueThrow);
            return effect;
        } else if (canSpawn) {
            PBEffectSpawnBlocks effect = new PBEffectSpawnBlocks(time, blocks, !spawnsFromEffectCenter);
            PBECSpawnEntities.setEffectSpawn(effect, random, valueSpawn);
            return effect;
        }

        throw new RuntimeException("Both spawnRange and throwStrength are null!");
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
