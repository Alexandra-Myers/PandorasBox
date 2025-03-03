/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenTransform;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECTransform(DValue range, EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks) implements PBEffectCreator {
    public static final MapCodec<PBECTransform> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECTransform::range),
                            WeightedBlock.CODEC.fieldOf("blocks").forGetter(PBECTransform::blocks))
                    .apply(instance, PBECTransform::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Block[] selection = PandorasBoxHelper.getRandomBlockList(random, PandorasBoxHelper.assembleBlocks(blocks));

        return new PBEffectGenTransform(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), selection);
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
