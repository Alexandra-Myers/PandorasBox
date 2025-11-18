package ivorius.pandorasbox.effects.generate.flags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface GenerateByFlag {
    Codec<GenerateByFlag> CODEC = Init.GEN_FLAGS_EFFECT_TYPE_REGISTRY.get().getCodec()
            .dispatch(GenerateByFlag::codec, MapCodec::codec);
    boolean hasFlag(Level world, PandorasBoxEntity entity, RandomSource random, BlockPos pos);
    void generateOnBlock(Level world, PandorasBoxEntity entity, RandomSource random, int pass, int unifiedSeed, BlockPos pos, double dist, boolean flag);
    int[] flags();
    @NotNull MapCodec<? extends GenerateByFlag> codec();
}
