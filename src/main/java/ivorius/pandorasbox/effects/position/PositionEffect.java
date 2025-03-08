package ivorius.pandorasbox.effects.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface PositionEffect {
    Codec<PositionEffect> CODEC = Init.POSITION_EFFECT_TYPE_REGISTRY.byNameCodec()
            .dispatch(PositionEffect::codec, Function.identity());
    void doEffect(ServerLevel serverLevel, PandorasBoxEntity entity, RandomSource random, float newRatio, float prevRatio, double x, double y, double z);

    @NotNull MapCodec<? extends PositionEffect> codec();
}
