package ivorius.pandorasbox.effectcreators.generate.two_dimensional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.two_dimensional.Generate2D;
import ivorius.pandorasbox.init.Init;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Generate2DCreator {
    Codec<Generate2DCreator> CODEC = Codec.withAlternative(Init.GEN_2D_EFFECT_CREATOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(Generate2DCreator::codec, Function.identity()), Generate2D.CODEC.xmap(DirectGenerate2DCreator::new, DirectGenerate2DCreator::generate2D));

    Generate2D constructGenerate2D(Level world, double x, double y, double z, RandomSource random);

    @NotNull MapCodec<? extends Generate2DCreator> codec();
}
