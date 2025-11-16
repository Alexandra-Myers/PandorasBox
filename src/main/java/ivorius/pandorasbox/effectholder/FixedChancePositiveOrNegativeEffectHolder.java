package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.network.chat.Component;

public class FixedChancePositiveOrNegativeEffectHolder extends EffectHolder {
    public static final DualMapCodec<FixedChancePositiveOrNegativeEffectHolder> CODEC = new DualMapCodec<>(RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.COMPONENT_CODEC.fieldOf("tooltip").forGetter(FixedChancePositiveOrNegativeEffectHolder::component),
                            PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(FixedChancePositiveOrNegativeEffectHolder::effectCreator),
                            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChancePositiveOrNegativeEffectHolder::fixedChance),
                            Codec.BOOL.fieldOf("positive").forGetter(FixedChancePositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, FixedChancePositiveOrNegativeEffectHolder::new)), RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(FixedChancePositiveOrNegativeEffectHolder::effectCreator),
                            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChancePositiveOrNegativeEffectHolder::fixedChance),
                            Codec.BOOL.fieldOf("positive").forGetter(FixedChancePositiveOrNegativeEffectHolder::isGood))
                    .apply(instance, FixedChancePositiveOrNegativeEffectHolder::new)));
    public final boolean good;
    public final double fixedChance;
    public FixedChancePositiveOrNegativeEffectHolder(Component component, PBEffectCreator effectCreator, double fixedChance, boolean good) {
        super(component, effectCreator);
        this.fixedChance = fixedChance;
        this.good = good;
    }

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
    public DualMapCodec<? extends EffectHolder> codec() {
        return CODEC;
    }

    @Override
    public EffectHolder wrapEffectCreator(PBEffectCreator replacementCreator) {
        return new FixedChancePositiveOrNegativeEffectHolder(component, replacementCreator, fixedChance, good);
    }
}
