/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesTeleport extends PBEffectEntityBased
{
    private double teleportRange;
    private int teleports;

    public PBEffectEntitiesTeleport(int maxTicksAlive, double range, double teleportRange, int teleports)
    {
        super(maxTicksAlive, range);
        this.teleportRange = teleportRange;
        this.teleports = teleports;
    }

    @Override
    public void affectEntity(World world, EntityPandorasBox box, Random random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        if (world instanceof ServerWorld)
        {
            Random entityRandom = new Random(entity.getId());

            for (int i = 0; i < teleports; i++)
            {
                double expectedTeleport = entityRandom.nextDouble();
                if (newRatio >= expectedTeleport && prevRatio < expectedTeleport)
                {
                    double newX = entity.getX() + (random.nextDouble() - random.nextDouble()) * teleportRange;
                    double newZ = entity.getZ() + (random.nextDouble() - random.nextDouble()) * teleportRange;
                    double newY = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(newX, 0.0, newZ)).getY() + 0.2;
                    float newYaw = random.nextFloat() * 360.0f;

                    entity.teleportTo(newX, newY, newZ);

                    if (entity instanceof ServerPlayerEntity)
                    {
                        ((ServerPlayerEntity) entity).connection.teleport(newX, newY, newZ, newYaw, entity.xRot);
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putDouble("teleportRange", teleportRange);
        compound.putInt("teleports", teleports);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        teleportRange = compound.getDouble("teleportRange");
        teleports = compound.getInt("teleports");
    }
}
