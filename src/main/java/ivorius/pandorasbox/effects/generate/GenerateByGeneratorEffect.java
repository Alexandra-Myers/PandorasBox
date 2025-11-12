package ivorius.pandorasbox.effects.generate;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface GenerateByGeneratorEffect<T> extends GenerateEffect {
    @Override
    default void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (level instanceof ServerLevel serverLevel) {
            if (random.nextDouble() < chancePerBlock()) {
                BlockState blockState = level.getBlockState(pos);
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = level.getBlockState(posBelow);

                if (blockState.isAir() && (!requiresSolidGround() || blockBelowState.isRedstoneConductor(level, posBelow))) {
                    T generator = getRandomGenerator(generators(), generatorFlags(), random);
                    generateGenerator(generator, serverLevel, random, pos);
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
    static <A extends GenerateByGeneratorEffect<T>, T> Products.P3<RecordCodecBuilder.Mu<A>, Boolean, Double, Integer> captureFieldsForCodec(RecordCodecBuilder.Instance<A> instance) {
        return instance.group(Codec.BOOL.fieldOf("requires_solid_ground").forGetter(GenerateByGeneratorEffect::requiresSolidGround),
                Codec.DOUBLE.fieldOf("chance_per_block").forGetter(GenerateByGeneratorEffect::chancePerBlock),
                Codec.INT.fieldOf("generator_flags").forGetter(GenerateByGeneratorEffect::generatorFlags));
    }
}
