/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.GenLavaCagesEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECLavaCage(DValue range, Optional<Block> lavaBlock, Optional<Block> fillBlock, EitherArrayList<WeightedBlock, WeightedTag<Block>> cageBlocks, EitherArrayList<WeightedBlock, WeightedTag<Block>> floorBlocks) implements PBEffectCreator {
    public static final MapCodec<PBECLavaCage> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECLavaCage::range),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("lava_block").forGetter(PBECLavaCage::lavaBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fill_block").forGetter(PBECLavaCage::fillBlock),
                            WeightedBlock.CODEC.fieldOf("cage_blocks").forGetter(PBECLavaCage::cageBlocks),
                            WeightedBlock.CODEC.fieldOf("floor_blocks").forGetter(PBECLavaCage::floorBlocks))
                    .apply(instance, PBECLavaCage::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Block cageBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(cageBlocks));
        Block floorBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(floorBlocks));

        return new PBEffectGenerate(time, range, 1, PandorasBoxHelper.getRandomUnifiedSeed(random), new GenLavaCagesEffect(lavaBlock, cageBlock, fillBlock, floorBlock, random.nextInt(10) + 2, random.nextInt(5) + 2));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
