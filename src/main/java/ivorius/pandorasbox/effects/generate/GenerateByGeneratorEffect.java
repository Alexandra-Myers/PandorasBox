package ivorius.pandorasbox.effects.generate;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;

public interface GenerateByGeneratorEffect<T> extends GenerateEffect {
    @Override
    default void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (level instanceof ServerLevel serverWorld) {
            if (random.nextDouble() < chancePerBlock()) {
                BlockState blockState = level.getBlockState(pos);
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = level.getBlockState(posBelow);

                if (blockState.isAir() && (!requiresSolidGround() || blockBelowState.isRedstoneConductor(level, posBelow))) {
                    T generator = getRandomGenerator(generators(), generatorFlags(), random);
                    generateGenerator(generator, serverWorld, random, pos);
                }
            }
        }
    }
    void generateGenerator(T generator, ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos);
    boolean requiresSolidGround();
    double chancePerBlock();
    int generatorFlags();
    List<T> generators();
    default T getRandomGenerator(List<T> generators, int flags, RandomSource random) {
        int totalNumber = 0;

        for (int i = 0; i < generators.size(); i++) {
            int flag = 1 << i;
            if ((flags & flag) > 0) {
                totalNumber++;
            }
        }

        int chosenGen = random.nextInt(totalNumber);
        for (int i = 0; i < generators.size(); i++) {
            int flag = 1 << i;
            if ((flags & flag) > 0) {
                if (chosenGen == 0) {
                    return generators.get(i);
                }

                chosenGen--;
            }
        }

        return null;
    }
    static <A extends GenerateByGeneratorEffect<T>, T> MapCodec<A> prepareCodec(Function<RecordCodecBuilder.Instance<A>, App<RecordCodecBuilder.Mu<A>, List<T>>> func, Function4<Boolean, Double, Integer, List<T>, A> constructor) {
        return RecordCodecBuilder.mapCodec(aInstance ->
                aInstance.group(Codec.BOOL.fieldOf("requires_solid_ground").forGetter(GenerateByGeneratorEffect::requiresSolidGround),
                                Codec.DOUBLE.fieldOf("chance_per_block").forGetter(GenerateByGeneratorEffect::chancePerBlock),
                                Codec.INT.fieldOf("generator_flags").forGetter(GenerateByGeneratorEffect::generatorFlags),
                                func.apply(aInstance))
                        .apply(aInstance, constructor));
    }
}
