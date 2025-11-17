package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import net.minecraft.network.chat.Component;

public class PositiveOrNegativeEffectHolder extends EffectHolder {
    public static final TriMapCodec<PositiveOrNegativeEffectHolder> CODEC = new TriMapCodec<>(RecordCodecBuilder.mapCodec(instance ->
            createTooltipCodec(instance).and(Codec.BOOL.fieldOf("positive").forGetter(PositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, PositiveOrNegativeEffectHolder::new)), RecordCodecBuilder.mapCodec(instance ->
            createNoTooltipCodec(instance).and(Codec.BOOL.fieldOf("positive").forGetter(PositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, PositiveOrNegativeEffectHolder::new)), RecordCodecBuilder.mapCodec(instance ->
            createNetworkCodec(instance).and(Codec.BOOL.fieldOf("positive").forGetter(PositiveOrNegativeEffectHolder::isGood))
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

    public PositiveOrNegativeEffectHolder(Component component, boolean good) {
        super(component);
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
    public TriMapCodec<? extends EffectHolder> codec() {
        return CODEC;
    }

    @Override
    public EffectHolder wrapEffectCreator(PBEffectCreator replacementCreator) {
        return new PositiveOrNegativeEffectHolder(component, replacementCreator, good);
    }
}
