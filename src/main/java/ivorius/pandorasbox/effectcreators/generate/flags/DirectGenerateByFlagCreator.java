package ivorius.pandorasbox.effectcreators.generate.flags;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.flags.GenerateByFlag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record DirectGenerateByFlagCreator(GenerateByFlag generateByFlag) implements GenerateByFlagCreator {
    public static final MapCodec<DirectGenerateByFlagCreator> CODEC = GenerateByFlag.CODEC.fieldOf("by_flag").xmap(DirectGenerateByFlagCreator::new, DirectGenerateByFlagCreator::generateByFlag);

    @Override
    public GenerateByFlag constructGenerateByFlag(Level world, double x, double y, double z, RandomSource random) {
        return generateByFlag;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateByFlagCreator> codec() {
        return CODEC;
    }
}
