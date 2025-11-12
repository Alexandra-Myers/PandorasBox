package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public record TeleportEntityEffect(double teleportRange, int teleports) implements EntityEffect {
    public static final MapCodec<TeleportEntityEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.DOUBLE.fieldOf("teleport_range").forGetter(TeleportEntityEffect::teleportRange),
                            Codec.INT.fieldOf("teleports").forGetter(TeleportEntityEffect::teleports))
                    .apply(instance, TeleportEntityEffect::new));
    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        Random entityRandom = new Random(entity.getId());

        for (int i = 0; i < teleports; i++) {
            double expectedTeleport = entityRandom.nextDouble();
            if (newRatio >= expectedTeleport && prevRatio < expectedTeleport) {
                double newX = entity.getX() + (random.nextDouble() - random.nextDouble()) * teleportRange;
                double newZ = entity.getZ() + (random.nextDouble() - random.nextDouble()) * teleportRange;
                double newY = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(newX, 0.0, newZ)).getY() + 0.2;
                float newYaw = random.nextFloat() * 360.0f;

                entity.teleportTo(newX, newY, newZ);

                if (entity instanceof ServerPlayer) {
                    ((ServerPlayer) entity).connection.teleport(newX, newY, newZ, newYaw, entity.getXRot());
                }
            }
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }

}
