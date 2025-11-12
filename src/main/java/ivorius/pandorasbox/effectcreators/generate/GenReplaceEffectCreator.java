package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.generate.GenReplaceEffect;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record GenReplaceEffectCreator(Optional<Block[]> srcBlocks, EitherArrayList<WeightedBlock, WeightedTag<Block>> destBlocks, ZValue takeRandomNearbyBlocks) implements GenerateEffectCreator {
    public static final MapCodec<GenReplaceEffectCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).optionalFieldOf("source_blocks").forGetter(GenReplaceEffectCreator::srcBlocks),
                            WeightedBlock.CODEC.fieldOf("destination_blocks").forGetter(GenReplaceEffectCreator::destBlocks),
                            ZValue.CODEC.fieldOf("take_random_nearby_blocks").forGetter(GenReplaceEffectCreator::takeRandomNearbyBlocks))
                    .apply(instance, GenReplaceEffectCreator::new));

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {

        int baseX = Mth.floor(x);
        int baseY = Mth.floor(y);
        int baseZ = Mth.floor(z);
        boolean takeRandomNearbyBlocks = this.takeRandomNearbyBlocks.getValue(random);

        Block[] srcSelection = new Block[0];

        if (takeRandomNearbyBlocks) {
            List<WeightedBlock> nearbyBlocks = new ArrayList<>();
            for (int xP = -5; xP <= 5; xP++) {
                for (int yP = -5; yP <= 5; yP++) {
                    for (int zP = -5; zP <= 5; zP++) {
                        BlockState block = world.getBlockState(new BlockPos(baseX + xP, baseY + yP, baseZ + zP));

                        if (!block.isAir())
                            nearbyBlocks.add(new WeightedBlock(100, block.getBlock()));
                    }
                }
            }

            if (!nearbyBlocks.isEmpty()) {
                srcSelection = PandorasBoxHelper.getRandomBlockList(random, nearbyBlocks);
            }
        } else {
            srcSelection = srcBlocks.orElse(new Block[0]).clone();
        }

        Block[] destSelection = PandorasBoxHelper.getRandomBlockList(random, PandorasBoxHelper.assembleBlocks(destBlocks));
        return new GenReplaceEffect(destSelection, srcSelection);
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}
