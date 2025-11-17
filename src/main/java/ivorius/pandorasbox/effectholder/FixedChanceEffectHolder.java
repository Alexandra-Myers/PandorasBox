package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import net.minecraft.network.chat.Component;

public class FixedChanceEffectHolder extends EffectHolder {
    public static final TriMapCodec<FixedChanceEffectHolder> CODEC = new TriMapCodec<>(RecordCodecBuilder.mapCodec(instance ->
            createTooltipCodec(instance).and(Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChanceEffectHolder::fixedChance))
                    .apply(instance, FixedChanceEffectHolder::new)), RecordCodecBuilder.mapCodec(instance ->
            createNoTooltipCodec(instance).and(Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChanceEffectHolder::fixedChance))
                    .apply(instance, FixedChanceEffectHolder::new)), RecordCodecBuilder.mapCodec(instance ->
            createNetworkCodec(instance).and(Codec.doubleRange(0, 1).fieldOf("chance").forGetter(FixedChanceEffectHolder::fixedChance))
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
    public FixedChanceEffectHolder(Component component, double fixedChance) {
        super(component);
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
    public TriMapCodec<?> codec() {
        return CODEC;
    }

    @Override
    public EffectHolder wrapEffectCreator(PBEffectCreator replacementCreator) {
        return new FixedChanceEffectHolder(component, replacementCreator, fixedChance);
    }
}
