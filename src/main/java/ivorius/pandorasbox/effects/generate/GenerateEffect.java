package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface GenerateEffect {
    Codec<GenerateEffect> CODEC = Init.GENERATE_EFFECT_TYPE_REGISTRY.byNameCodec()
            .dispatch(GenerateEffect::codec, Function.identity());
    void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed);
    @Nullable ResourceKey<Biome> biome();
    @NotNull MapCodec<? extends GenerateEffect> codec();
}
