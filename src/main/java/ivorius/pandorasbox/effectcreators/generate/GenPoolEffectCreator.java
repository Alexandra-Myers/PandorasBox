package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.GenPoolEffect;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public record GenPoolEffectCreator(Block block, EitherArrayList<WeightedBlock, WeightedTag<Block>> platformBlocks) implements GenerateEffectCreator {
    public static final MapCodec<GenPoolEffectCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(GenPoolEffectCreator::block),
                            WeightedBlock.CODEC.fieldOf("platform_blocks").forGetter(GenPoolEffectCreator::platformBlocks))
                    .apply(instance, GenPoolEffectCreator::new));

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {
        Block platformBlock = platformBlocks.isEmpty() ? Blocks.AIR : PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(platformBlocks));
        return new GenPoolEffect(block, platformBlock);
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}
