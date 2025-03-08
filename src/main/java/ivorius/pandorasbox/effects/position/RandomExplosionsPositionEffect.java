package ivorius.pandorasbox.effects.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record RandomExplosionsPositionEffect(float minExplosionStrength, float maxExplosionStrength, boolean isFlaming, boolean isSmoking) implements PositionEffect {
    public static final MapCodec<RandomExplosionsPositionEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.FLOAT.fieldOf("min_explosion_strength").forGetter(RandomExplosionsPositionEffect::minExplosionStrength),
                            Codec.FLOAT.fieldOf("max_explosion_strength").forGetter(RandomExplosionsPositionEffect::maxExplosionStrength),
                            Codec.BOOL.fieldOf("flaming").forGetter(RandomExplosionsPositionEffect::isFlaming),
                            Codec.BOOL.fieldOf("smoking").forGetter(RandomExplosionsPositionEffect::isSmoking))
                    .apply(instance, RandomExplosionsPositionEffect::new));
    @Override
    public void doEffect(ServerLevel serverLevel, PandorasBoxEntity entity, RandomSource random, float newRatio, float prevRatio, double x, double y, double z) {
        serverLevel.explode(entity, x, y, z, minExplosionStrength + random.nextFloat() * (maxExplosionStrength - minExplosionStrength), isFlaming, Level.ExplosionInteraction.TNT);
    }

    @Override
    public @NotNull MapCodec<? extends PositionEffect> codec() {
        return CODEC;
    }
}
