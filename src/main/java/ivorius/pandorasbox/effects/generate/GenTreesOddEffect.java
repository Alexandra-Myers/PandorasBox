package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.init.FeatureInit;
import ivorius.pandorasbox.worldgen.MegaTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record GenTreesOddEffect(boolean requiresSolidGround, double chancePerBlock, int generatorFlags, Block trunkBlock, Block leavesBlock, List<MegaTreeFeature> generators) implements GenerateByGeneratorEffect<MegaTreeFeature> {

    public static final MapCodec<GenTreesOddEffect> CODEC = RecordCodecBuilder.mapCodec(aInstance ->
            aInstance.group(Codec.BOOL.fieldOf("requires_solid_ground").forGetter(GenTreesOddEffect::requiresSolidGround),
                            Codec.DOUBLE.fieldOf("chance_per_block").forGetter(GenTreesOddEffect::chancePerBlock),
                            Codec.INT.fieldOf("generator_flags").forGetter(GenTreesOddEffect::generatorFlags),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("trunk").forGetter(GenTreesOddEffect::trunkBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("leaves").forGetter(GenTreesOddEffect::leavesBlock))
                    .apply(aInstance, GenTreesOddEffect::new));
    public GenTreesOddEffect(boolean requiresSolidGround, double chancePerBlock, int generatorFlags, Block trunkBlock, Block leavesBlock) {
        this(requiresSolidGround, chancePerBlock, generatorFlags, trunkBlock, leavesBlock, new ArrayList<>());
        generators().addFirst((MegaTreeFeature) FeatureInit.MEGA_JUNGLE);
        generators().getFirst().setTrunk(trunkBlock.defaultBlockState());
        generators().getFirst().setLeaves(leavesBlock.defaultBlockState());
    }
    @Override
    public @Nullable ResourceKey<Biome> biome() {
        return null;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffect> codec() {
        return CODEC;
    }

    @Override
    public void generateGenerator(MegaTreeFeature generator, ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos) {
        generator.place(serverLevel, randomSource, blockPos);
    }
}
