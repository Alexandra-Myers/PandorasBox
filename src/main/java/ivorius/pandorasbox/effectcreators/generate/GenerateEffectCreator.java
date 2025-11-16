package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.init.Init;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface GenerateEffectCreator {
    Codec<GenerateEffectCreator> CODEC = Init.GENERATE_EFFECT_CREATOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(GenerateEffectCreator::codec, MapCodec::codec);
    GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random);
    default int getPasses() {
        return 1;
    }
    @NotNull MapCodec<? extends GenerateEffectCreator> codec();
}
