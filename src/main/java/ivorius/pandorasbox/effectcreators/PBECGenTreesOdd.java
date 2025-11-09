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
import ivorius.pandorasbox.effects.generate.GenTreesOddEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZConstant;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.data.worldgen.features.TreeFeatures.*;
import static net.minecraft.data.worldgen.features.TreeFeatures.MEGA_PINE;
import static net.minecraft.data.worldgen.features.TreeFeatures.MEGA_SPRUCE;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECGenTreesOdd(DValue range, DValue chancePerBlock, ZValue requiresSolidGround, IValue possibleTreeFlags, EitherArrayList<WeightedBlock, WeightedTag<Block>> trunkBlocks, EitherArrayList<WeightedBlock, WeightedTag<Block>> leafBlocks) implements PBEffectCreator {
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
        boolean requiresSolidGround = this.requiresSolidGround.getValue(random);
        double chancePerBlock = this.chancePerBlock.getValue(random) * (!requiresSolidGround ? 0.01 : 1);
        int possibleTreeFlags = this.possibleTreeFlags.getValue(random);

        Block trunkBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(trunkBlocks));
        Block leafBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(leafBlocks));

        return new PBEffectGenerate(time, range, 1, PandorasBoxHelper.getRandomUnifiedSeed(random), new GenTreesOddEffect(requiresSolidGround, chancePerBlock, possibleTreeFlags, trunkBlock, leafBlock, List.of(MEGA_JUNGLE_TREE, JUNGLE_TREE, JUNGLE_BUSH, CHERRY, FANCY_OAK_BEES, DARK_OAK, SPRUCE, BIRCH, MEGA_PINE, MEGA_SPRUCE)));
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
