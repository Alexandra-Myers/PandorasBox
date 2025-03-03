/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenReplace;
import ivorius.pandorasbox.random.DValue;
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

/**
 * Created by lukas on 30.03.14.
 */
public record PBECReplace(DValue range, Optional<Block[]> srcBlocks, EitherArrayList<WeightedBlock, WeightedTag<Block>> destBlocks, ZValue takeRandomNearbyBlocks) implements PBEffectCreator {
    public static final MapCodec<PBECReplace> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECReplace::range),
                            PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).optionalFieldOf("source_blocks").forGetter(PBECReplace::srcBlocks),
                            WeightedBlock.CODEC.fieldOf("destination_blocks").forGetter(PBECReplace::destBlocks),
                            ZValue.CODEC.fieldOf("take_random_nearby_blocks").forGetter(PBECReplace::takeRandomNearbyBlocks))
                    .apply(instance, PBECReplace::new));
    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

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

        return new PBEffectGenReplace(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), destSelection, srcSelection);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
