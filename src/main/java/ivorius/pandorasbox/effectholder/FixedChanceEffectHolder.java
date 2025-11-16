package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.network.chat.Component;

public class FixedChanceEffectHolder extends EffectHolder {
    public static final DualMapCodec<FixedChanceEffectHolder> CODEC = new DualMapCodec<>(RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.COMPONENT_CODEC.fieldOf("tooltip").forGetter(FixedChanceEffectHolder::component),
                            PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(FixedChanceEffectHolder::effectCreator),
                            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChanceEffectHolder::fixedChance))
                    .apply(instance, FixedChanceEffectHolder::new)), RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBEffectCreator.CODEC.fieldOf("effect_creator").forGetter(FixedChanceEffectHolder::effectCreator),
                            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChanceEffectHolder::fixedChance))
                    .apply(instance, FixedChanceEffectHolder::new)));
    public final double fixedChance;
    public FixedChanceEffectHolder(Component component, PBEffectCreator pbEffectCreator, double fixedChance) {
        super(component, pbEffectCreator);
        this.fixedChance = fixedChance;
    }
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
    public DualMapCodec<?> codec() {
        return CODEC;
    }

    @Override
    public EffectHolder wrapEffectCreator(PBEffectCreator replacementCreator) {
        return new FixedChanceEffectHolder(component, replacementCreator, fixedChance);
    }
}
