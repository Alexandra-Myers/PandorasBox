package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.GenTreesOddEffect;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZConstant;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GenTreesOddEffectCreator(DValue chancePerBlock, ZValue requiresSolidGround, IValue possibleTreeFlags, EitherArrayList<WeightedBlock, WeightedTag<Block>> trunkBlocks, EitherArrayList<WeightedBlock, WeightedTag<Block>> leafBlocks, List<ResourceKey<ConfiguredFeature<?, ?>>> features) implements GenerateEffectCreator {
    public static final MapCodec<GenTreesOddEffectCreator> CODEC = RecordCodecBuilder.mapCodec(aInstance ->
            aInstance.group(DValue.CODEC.fieldOf("chance_per_block").forGetter(GenTreesOddEffectCreator::chancePerBlock),
                            ZValue.CODEC.optionalFieldOf("requires_solid_ground", new ZConstant(true)).forGetter(GenTreesOddEffectCreator::requiresSolidGround),
                            IValue.CODEC.fieldOf("possible_tree_flags").forGetter(GenTreesOddEffectCreator::possibleTreeFlags),
                            WeightedBlock.CODEC.fieldOf("trunk_blocks").forGetter(GenTreesOddEffectCreator::trunkBlocks),
                            WeightedBlock.CODEC.fieldOf("leaf_blocks").forGetter(GenTreesOddEffectCreator::leafBlocks),
                            ResourceKey.codec(Registries.CONFIGURED_FEATURE).listOf().fieldOf("tree_features").forGetter(GenTreesOddEffectCreator::features))
                    .apply(aInstance, GenTreesOddEffectCreator::new));

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {
        boolean requiresSolidGround = this.requiresSolidGround.getValue(random);
        double chancePerBlock = this.chancePerBlock.getValue(random) * (!requiresSolidGround ? 0.01 : 1);
        int possibleTreeFlags = this.possibleTreeFlags.getValue(random);

        Block trunkBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(trunkBlocks));
        Block leafBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(leafBlocks));
        return new GenTreesOddEffect(requiresSolidGround, chancePerBlock, possibleTreeFlags, trunkBlock, leafBlock, features);
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}
