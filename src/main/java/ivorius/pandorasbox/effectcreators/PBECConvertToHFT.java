/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenConvertToHFT;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToHFT(DValue range) implements PBEffectCreator {
    public static final MapCodec<PBECConvertToHFT> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToHFT::new, PBECConvertToHFT::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        int[] metaTypes = new int[random.nextInt(3) + 2];
        for (int i = 0; i < metaTypes.length; i++) {
            metaTypes[i] = random.nextInt(32);
        }

        return new PBEffectGenConvertToHFT(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), metaTypes);
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
