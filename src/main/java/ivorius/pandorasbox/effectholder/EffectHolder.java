package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public abstract class EffectHolder {
    public static final ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends EffectHolder>> HOLDER_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    public static final Codec<EffectHolder> CODEC = HOLDER_MAPPER.codec(ResourceLocation.CODEC)
            .dispatch(EffectHolder::codec, mapCodec -> mapCodec);

    protected EffectHolder(PBEffectCreator effectCreator) {
        this.effectCreator = effectCreator;
    }

    public static void bootstrap() {
        HOLDER_MAPPER.put(ResourceLocation.withDefaultNamespace("fixed_chance"), FixedChanceEffectHolder.CODEC);
        HOLDER_MAPPER.put(ResourceLocation.withDefaultNamespace("fixed_chance_marked"), FixedChancePositiveOrNegativeEffectHolder.CODEC);
        HOLDER_MAPPER.put(ResourceLocation.withDefaultNamespace("positive_or_negative"), PositiveOrNegativeEffectHolder.CODEC);
    }
    public final PBEffectCreator effectCreator;
    public PBEffectCreator effectCreator() {
        return effectCreator;
    }
    public abstract boolean canBeGoodOrBad();
    public abstract boolean isGood();
    public abstract double fixedChance();
    public abstract MapCodec<? extends EffectHolder> codec();
}
