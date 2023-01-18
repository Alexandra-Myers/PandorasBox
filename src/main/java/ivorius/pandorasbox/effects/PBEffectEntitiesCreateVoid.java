/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesCreateVoid extends PBEffectEntityBased
{

    public PBEffectEntitiesCreateVoid(int maxTicksAlive, double range)
    {
        super(maxTicksAlive, range);
    }

    @Override
    public void affectEntity(World world, EntityPandorasBox box, Random random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        if (world instanceof ServerWorld)
        {
            if (entity instanceof PlayerEntity)
            {
                int baseY = MathHelper.floor(entity.getY());
                int baseX = MathHelper.floor(entity.getX());
                int baseZ = MathHelper.floor(entity.getZ());

                for (int x = -1; x <= 1; x++)
                {
                    for (int y = -8; y <= 2; y++)
                    {
                        for (int z = -1; z <= 1; z++)
                        {
                            setBlockToAirSafe(world, new BlockPos(baseX + x, baseY + y, baseZ + z));
                        }
                    }
                }
            }
        }
    }
}
