/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenCreativeTowers;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECCreativeTowers(DValue range, IValue number, List<Either<WeightedBlock, WeightedTag<Block>>> blocks) implements PBEffectCreator {
    public static final MapCodec<PBECCreativeTowers> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECCreativeTowers::range),
                            IValue.CODEC.fieldOf("number").forGetter(PBECCreativeTowers::number),
                            WeightedBlock.CODEC.fieldOf("blocks").forGetter(PBECCreativeTowers::blocks))
                    .apply(instance, PBECCreativeTowers::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int number = this.number.getValue(random);
        int time = Mth.floor((random.nextDouble() * 4.0 + 1.0) * number * 10.0);

        PBEffectGenCreativeTowers genCreativeTowers = new PBEffectGenCreativeTowers(time);
        genCreativeTowers.createRandomStructures(random, number, range, PandorasBoxHelper.assembleBlocks(blocks));
        return genCreativeTowers;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
