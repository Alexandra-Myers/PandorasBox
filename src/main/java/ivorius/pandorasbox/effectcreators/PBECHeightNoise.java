/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate2D;
import ivorius.pandorasbox.effects.generate.two_dimensional.GenHeightNoise;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECHeightNoise(DValue range, IValue shift, IValue towerSize, IValue blockSize) implements PBEffectCreator {
    public static final MapCodec<PBECHeightNoise> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECHeightNoise::range),
                            IValue.CODEC.fieldOf("shift").forGetter(PBECHeightNoise::shift),
                            IValue.CODEC.fieldOf("tower_size").forGetter(PBECHeightNoise::towerSize),
                            IValue.CODEC.fieldOf("block_size").forGetter(PBECHeightNoise::blockSize))
                    .apply(instance, PBECHeightNoise::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        int blockSize = this.blockSize.getValue(random);

        int[] shift = ValueHelper.getValueRange(this.shift, random);
        int[] towerSize = ValueHelper.getValueRange(this.towerSize, random);

        return new PBEffectGenerate2D(time, range, 1, PandorasBoxHelper.getRandomUnifiedSeed(random), new GenHeightNoise(shift[0], shift[1], towerSize[0], towerSize[1], blockSize));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
