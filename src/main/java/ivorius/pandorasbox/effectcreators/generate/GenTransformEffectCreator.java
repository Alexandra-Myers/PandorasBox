package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.GenTransformEffect;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record GenTransformEffectCreator(EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks) implements GenerateEffectCreator {
    public static final MapCodec<GenTransformEffectCreator> CODEC = WeightedBlock.CODEC.fieldOf("blocks").xmap(GenTransformEffectCreator::new, GenTransformEffectCreator::blocks);

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {
        Block[] selection = PandorasBoxHelper.getRandomBlockList(random, PandorasBoxHelper.assembleBlocks(blocks));

        return new GenTransformEffect(selection);
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}
