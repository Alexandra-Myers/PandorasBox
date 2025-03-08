package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class PositiveOrNegativeEffectHolder extends EffectHolder {
    public static final MapCodec<PositiveOrNegativeEffectHolder> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ComponentSerialization.CODEC.fieldOf("tooltip").forGetter(PositiveOrNegativeEffectHolder::component),
                            PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(PositiveOrNegativeEffectHolder::effectCreator),
                            Codec.BOOL.fieldOf("positive").forGetter(PositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, PositiveOrNegativeEffectHolder::new));
    public final boolean good;
    public PositiveOrNegativeEffectHolder(Component component, PBEffectCreator effectCreator, boolean good) {
        super(component, effectCreator);
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
