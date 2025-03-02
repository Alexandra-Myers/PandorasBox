/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenTreesOdd;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZConstant;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECGenTreesOdd(DValue range, DValue chancePerBlock, ZValue requiresSolidGround, IValue possibleTreeFlags, List<Either<WeightedBlock, WeightedTag<Block>>> trunkBlocks, List<Either<WeightedBlock, WeightedTag<Block>>> leafBlocks) implements PBEffectCreator {
    public static final MapCodec<PBECGenTreesOdd> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECGenTreesOdd::range),
                            DValue.CODEC.fieldOf("chance_per_block").forGetter(PBECGenTreesOdd::chancePerBlock),
                            ZValue.CODEC.optionalFieldOf("requires_solid_ground", new ZConstant(true)).forGetter(PBECGenTreesOdd::requiresSolidGround),
                            IValue.CODEC.fieldOf("possible_tree_flags").forGetter(PBECGenTreesOdd::possibleTreeFlags),
                            WeightedBlock.CODEC.fieldOf("trunk_blocks").forGetter(PBECGenTreesOdd::trunkBlocks),
                            WeightedBlock.CODEC.fieldOf("leaf_blocks").forGetter(PBECGenTreesOdd::leafBlocks))
                    .apply(instance, PBECGenTreesOdd::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        double chancePerBlock = this.chancePerBlock.getValue(random);
        boolean requiresSolidGround = this.requiresSolidGround.getValue(random);
        int possibleTreeFlags = this.possibleTreeFlags.getValue(random);

        Block trunkBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(trunkBlocks));
        Block leafBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(leafBlocks));

        return new PBEffectGenTreesOdd(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), requiresSolidGround, chancePerBlock, possibleTreeFlags, trunkBlock, leafBlock);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
