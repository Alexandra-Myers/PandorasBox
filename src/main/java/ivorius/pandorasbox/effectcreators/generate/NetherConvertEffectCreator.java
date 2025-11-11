package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.effects.generate.NetherBiome;
import ivorius.pandorasbox.effects.generate.NetherConvertEffect;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record NetherConvertEffectCreator(DValue chanceToDiscardNetherrack, NetherBiome biome) implements GenerateEffectCreator {
    public static final MapCodec<NetherConvertEffectCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("chance_to_discard_netherrack").forGetter(NetherConvertEffectCreator::chanceToDiscardNetherrack),
                            NetherBiome.CODEC.fieldOf("biome").forGetter(NetherConvertEffectCreator::biome))
                    .apply(instance, NetherConvertEffectCreator::new));

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {
        return new NetherConvertEffect(this.biome, this.chanceToDiscardNetherrack.getValue(random));
    }

    @Override
    public int getPasses() {
        return 3;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}
