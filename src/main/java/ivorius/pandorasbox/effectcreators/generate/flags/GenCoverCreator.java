package ivorius.pandorasbox.effectcreators.generate.flags;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.flags.GenCover;
import ivorius.pandorasbox.effects.generate.flags.GenerateByFlag;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record GenCoverCreator(ZValue overSurface, EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks) implements GenerateByFlagCreator {
    public static final MapCodec<GenCoverCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ZValue.CODEC.fieldOf("over_surface").forGetter(GenCoverCreator::overSurface),
                            WeightedBlock.CODEC.fieldOf("blocks").forGetter(GenCoverCreator::blocks))
                    .apply(instance, GenCoverCreator::new));

    @Override
    public GenerateByFlag constructGenerateByFlag(Level world, double x, double y, double z, RandomSource random) {
        boolean overSurface = this.overSurface.getValue(random);

        Block[] selection = PandorasBoxHelper.getRandomBlockList(random, PandorasBoxHelper.assembleBlocks(blocks));
        return new GenCover(overSurface, selection);
    }

    @Override
    public @NotNull MapCodec<? extends GenerateByFlagCreator> codec() {
        return CODEC;
    }
}
