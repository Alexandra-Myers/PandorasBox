package ivorius.pandorasbox.effects.generate.feature_generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static ivorius.pandorasbox.effects.generate.NetherBiome.expFromRatio;

public interface FeatureGenerator {
    Codec<FeatureGenerator> CODEC = Init.FEATURE_GENERATOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(FeatureGenerator::codec, Function.identity());
    default void generate(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (world instanceof ServerLevel serverLevel && random.nextDouble() < Math.pow(baseChance(), expFromRatio(ratio))) finalGenerate(serverLevel, pos, serverLevel.getBlockState(pos), random, entity, effectCenter, pass, unifiedSeed, range);
    }

    void finalGenerate(ServerLevel serverLevel, BlockPos pos, BlockState blockState, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range);

    double baseChance();

    @NotNull MapCodec<? extends FeatureGenerator> codec();
}
