package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;

public class FixedChancePositiveOrNegativeEffectHolder extends EffectHolder {
    public static final MapCodec<FixedChancePositiveOrNegativeEffectHolder> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(FixedChancePositiveOrNegativeEffectHolder::effectCreator),
                            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChancePositiveOrNegativeEffectHolder::fixedChance),
                            Codec.BOOL.fieldOf("positive").forGetter(FixedChancePositiveOrNegativeEffectHolder::canBeGoodOrBad))
                    .apply(instance, FixedChancePositiveOrNegativeEffectHolder::new));
    public final boolean good;
    public final double fixedChance;
    public FixedChancePositiveOrNegativeEffectHolder(PBEffectCreator effectCreator, double fixedChance, boolean good) {
        super(effectCreator);
        this.fixedChance = fixedChance;
        this.good = good;
    }

    @Override
    public boolean canBeGoodOrBad() {
        return true;
    }

    @Override
    public boolean isGood() {
        return good;
    }

    @Override
    public double fixedChance() {
        return fixedChance;
    }

    @Override
    public MapCodec<? extends EffectHolder> codec() {
        return CODEC;
    }
}
