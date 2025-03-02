package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;

public class PositiveOrNegativeEffectHolder extends EffectHolder {
    public static final MapCodec<PositiveOrNegativeEffectHolder> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(PositiveOrNegativeEffectHolder::effectCreator),
                            Codec.BOOL.fieldOf("positive").forGetter(PositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, PositiveOrNegativeEffectHolder::new));
    public final boolean good;
    public PositiveOrNegativeEffectHolder (PBEffectCreator effectCreator, boolean good) {
        super(effectCreator);
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
        return -1;
    }

    @Override
    public MapCodec<? extends EffectHolder> codec() {
        return CODEC;
    }
}
