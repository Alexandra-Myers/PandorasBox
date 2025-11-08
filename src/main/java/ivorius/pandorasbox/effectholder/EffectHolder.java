package ivorius.pandorasbox.effectholder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public abstract class EffectHolder {
    public static final ExtraCodecs.LateBoundIdMapper<Identifier, DualMapCodec<? extends EffectHolder>> HOLDER_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    public static final Codec<EffectHolder> DIRECT_CODEC = HOLDER_MAPPER.codec(Identifier.CODEC)
            .dispatch(EffectHolder::codec, mapCodec -> mapCodec.withTooltip);
    public static final Codec<EffectHolder> DIRECT_CODEC_NO_TOOLTIP = HOLDER_MAPPER.codec(Identifier.CODEC)
            .dispatch(EffectHolder::codec, mapCodec -> mapCodec.withoutTooltip);
    public static final Codec<HolderSet<EffectHolder>> CODEC = RegistryCodecs.homogeneousList(Init.EFFECT_HOLDER_REGISTRY_KEY);
    public static final Codec<HolderSet<EffectHolder>> MELTDOWN_CODEC = RegistryCodecs.homogeneousList(Init.MELTDOWN_EFFECT_HOLDER_REGISTRY_KEY);
    public final PBEffectCreator effectCreator;
    public final Component component;
    public Component component() {
        return component;
    }
    public PBEffectCreator effectCreator() {
        return effectCreator;
    }
    protected EffectHolder(Component component, PBEffectCreator effectCreator) {
        this.component = component;
        this.effectCreator = effectCreator;
    }
    protected EffectHolder(PBEffectCreator effectCreator) {
        this.component = Component.translatable("effect_holder.pandorasbox.empty");
        this.effectCreator = effectCreator;
    }

    public static void bootstrap() {
        HOLDER_MAPPER.put(Identifier.withDefaultNamespace("fixed_chance"), FixedChanceEffectHolder.CODEC);
        HOLDER_MAPPER.put(Identifier.withDefaultNamespace("fixed_chance_marked"), FixedChancePositiveOrNegativeEffectHolder.CODEC);
        HOLDER_MAPPER.put(Identifier.withDefaultNamespace("positive_or_negative"), PositiveOrNegativeEffectHolder.CODEC);
    }
    public abstract boolean canBeGoodOrBad();
    public abstract boolean isGood();
    public abstract double fixedChance();
    public abstract DualMapCodec<?> codec();
    public abstract EffectHolder wrapEffectCreator(PBEffectCreator replacementCreator);

    public record DualMapCodec<E extends EffectHolder>(MapCodec<E> withTooltip, MapCodec<E> withoutTooltip) {

    }
}
