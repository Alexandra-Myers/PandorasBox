package ivorius.pandorasbox.effectcreators.generate.two_dimensional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.two_dimensional.GenDome;
import ivorius.pandorasbox.effects.generate.two_dimensional.Generate2D;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record GenDomeCreator(EitherArrayList<WeightedBlock, WeightedTag<Block>> domeBlocks, Optional<Block> fillBlock) implements Generate2DCreator {
    public static final MapCodec<GenDomeCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(WeightedBlock.CODEC.fieldOf("dome_blocks").forGetter(GenDomeCreator::domeBlocks),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fill_block").forGetter(GenDomeCreator::fillBlock))
                    .apply(instance, GenDomeCreator::new));

    @Override
    public Generate2D constructGenerate2D(Level world, double x, double y, double z, RandomSource random) {
        Block domeBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(domeBlocks));
        return new GenDome(domeBlock, fillBlock);
    }

    @Override
    public @NotNull MapCodec<? extends Generate2DCreator> codec() {
        return CODEC;
    }
}
