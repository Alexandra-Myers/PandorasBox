/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesTeleport extends PBEffectEntityBased {
    private double teleportRange;
    private int teleports;
    public PBEffectEntitiesTeleport() {}

    public PBEffectEntitiesTeleport(int maxTicksAlive, double range, double teleportRange, int teleports) {
        super(maxTicksAlive, range);
        this.teleportRange = teleportRange;
        this.teleports = teleports;
    }

    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
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
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putDouble("teleportRange", teleportRange);
        compound.putInt("teleports", teleports);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        teleportRange = compound.getDouble("teleportRange");
        teleports = compound.getInt("teleports");
    }
}
