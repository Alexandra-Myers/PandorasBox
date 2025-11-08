package ivorius.pandorasbox.effectcreators.generate.flags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.flags.GenerateByFlag;
import ivorius.pandorasbox.init.Init;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface GenerateByFlagCreator {
    Codec<GenerateByFlagCreator> CODEC = Codec.withAlternative(Init.GEN_FLAGS_EFFECT_CREATOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(GenerateByFlagCreator::codec, Function.identity()), GenerateByFlag.CODEC.xmap(DirectGenerateByFlagCreator::new, DirectGenerateByFlagCreator::generateByFlag));

    GenerateByFlag constructGenerateByFlag(Level world, double x, double y, double z, RandomSource random);

    @NotNull MapCodec<? extends GenerateByFlagCreator> codec();
}
