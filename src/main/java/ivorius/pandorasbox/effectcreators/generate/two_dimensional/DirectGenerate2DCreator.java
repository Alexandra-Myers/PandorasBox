package ivorius.pandorasbox.effectcreators.generate.two_dimensional;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.two_dimensional.Generate2D;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record DirectGenerate2DCreator(Generate2D generate2D) implements Generate2DCreator {
    public static final MapCodec<DirectGenerate2DCreator> CODEC = Generate2D.CODEC.fieldOf("two_dimensional").xmap(DirectGenerate2DCreator::new, DirectGenerate2DCreator::generate2D);

    @Override
    public Generate2D constructGenerate2D(Level world, double x, double y, double z, RandomSource random) {
        return generate2D;
    }

    @Override
    public @NotNull MapCodec<? extends Generate2DCreator> codec() {
        return CODEC;
    }
}
