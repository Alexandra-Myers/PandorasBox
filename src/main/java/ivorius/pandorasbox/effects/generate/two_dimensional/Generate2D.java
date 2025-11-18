package ivorius.pandorasbox.effects.generate.two_dimensional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface Generate2D {
    Codec<Generate2D> CODEC = Init.GEN_2D_EFFECT_TYPE_REGISTRY.get().getCodec()
            .dispatch(Generate2D::codec, MapCodec::codec);
    void generateOnSurface(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, BlockPos pos, double distance, double range, int pass, int unifiedSeed);
    @NotNull MapCodec<? extends Generate2D> codec();
}
