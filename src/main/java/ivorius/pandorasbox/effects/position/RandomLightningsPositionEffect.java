package ivorius.pandorasbox.effects.position;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record RandomLightningsPositionEffect() implements PositionEffect {
    public static final MapCodec<RandomLightningsPositionEffect> CODEC = MapCodec.unit(RandomLightningsPositionEffect::new);

    @Override
    public void doEffect(ServerLevel serverLevel, PandorasBoxEntity entity, RandomSource random, float newRatio, float prevRatio, double x, double y, double z) {
        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.NATURAL);
        if (lightningBolt == null) return;
        lightningBolt.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
        serverLevel.addFreshEntity(lightningBolt);
    }

    @Override
    public @NotNull MapCodec<? extends PositionEffect> codec() {
        return CODEC;
    }
}
