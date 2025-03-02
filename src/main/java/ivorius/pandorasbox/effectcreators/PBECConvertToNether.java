/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenConvertToNether;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToNether(DValue range, DValue chanceToDiscardNetherrack, PBEffectGenConvertToNether.NetherBiome biome) implements PBEffectCreator {
    public static final MapCodec<PBECConvertToNether> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECConvertToNether::range),
                            DValue.CODEC.fieldOf("chance_to_discard_netherrack").forGetter(PBECConvertToNether::chanceToDiscardNetherrack),
                            PBEffectGenConvertToNether.NetherBiome.CODEC.fieldOf("biome").forGetter(PBECConvertToNether::biome))
                    .apply(instance, PBECConvertToNether::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        double discardChance = chanceToDiscardNetherrack.getValue(random);

        return new PBEffectGenConvertToNether(time, range, discardChance, PandorasBoxHelper.getRandomUnifiedSeed(random), biome);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
