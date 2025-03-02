package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;

public class FixedChanceEffectHolder extends EffectHolder {
    public static final MapCodec<FixedChanceEffectHolder> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(FixedChanceEffectHolder::effectCreator),
                            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChanceEffectHolder::fixedChance))
                    .apply(instance, FixedChanceEffectHolder::new));
    public final double fixedChance;
    public FixedChanceEffectHolder(PBEffectCreator pbEffectCreator, double fixedChance) {
        super(pbEffectCreator);
        this.fixedChance = fixedChance;
    }

    @Override
    public boolean canBeGoodOrBad() {
        return false;
    }

    @Override
    public boolean isGood() {
        return false;
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
