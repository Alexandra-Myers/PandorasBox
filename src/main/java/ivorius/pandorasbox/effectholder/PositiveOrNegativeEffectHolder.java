package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.network.chat.Component;

public class PositiveOrNegativeEffectHolder extends EffectHolder {
    public static final DualMapCodec<PositiveOrNegativeEffectHolder> CODEC = new DualMapCodec<>(RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.COMPONENT_CODEC.fieldOf("tooltip").forGetter(PositiveOrNegativeEffectHolder::component),
                            PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(PositiveOrNegativeEffectHolder::effectCreator),
                            Codec.BOOL.fieldOf("positive").forGetter(PositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, PositiveOrNegativeEffectHolder::new)), RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(PositiveOrNegativeEffectHolder::effectCreator),
                            Codec.BOOL.fieldOf("positive").forGetter(PositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, PositiveOrNegativeEffectHolder::new)));
    public final boolean good;
    public PositiveOrNegativeEffectHolder(Component component, PBEffectCreator effectCreator, boolean good) {
        super(component, effectCreator);
        this.good = good;
    }

    public PositiveOrNegativeEffectHolder(PBEffectCreator effectCreator, boolean good) {
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
    public DualMapCodec<? extends EffectHolder> codec() {
        return CODEC;
    }

    @Override
    public EffectHolder wrapEffectCreator(PBEffectCreator replacementCreator) {
        return new PositiveOrNegativeEffectHolder(component, replacementCreator, good);
    }
}
