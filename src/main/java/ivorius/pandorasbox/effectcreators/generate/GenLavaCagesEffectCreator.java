package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.GenLavaCagesEffect;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record GenLavaCagesEffectCreator(Optional<Block> lavaBlock, Optional<Block> fillBlock, EitherArrayList<WeightedBlock, WeightedTag<Block>> cageBlocks, EitherArrayList<WeightedBlock, WeightedTag<Block>> floorBlocks, IValue heightOffset, IValue wallDist) implements GenerateEffectCreator {
    public static final MapCodec<GenLavaCagesEffectCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("lava_block").forGetter(GenLavaCagesEffectCreator::lavaBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fill_block").forGetter(GenLavaCagesEffectCreator::fillBlock),
                            WeightedBlock.CODEC.fieldOf("cage_blocks").forGetter(GenLavaCagesEffectCreator::cageBlocks),
                            WeightedBlock.CODEC.fieldOf("floor_blocks").forGetter(GenLavaCagesEffectCreator::floorBlocks),
                            IValue.CODEC.fieldOf("height_offset").forGetter(GenLavaCagesEffectCreator::heightOffset),
                            IValue.CODEC.fieldOf("wall_dist").forGetter(GenLavaCagesEffectCreator::wallDist))
                    .apply(instance, GenLavaCagesEffectCreator::new));

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {
        Block cageBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(cageBlocks));
        Block floorBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(floorBlocks));
        return new GenLavaCagesEffect(lavaBlock, cageBlock, fillBlock, floorBlock, heightOffset.getValue(random), wallDist.getValue(random));
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}
