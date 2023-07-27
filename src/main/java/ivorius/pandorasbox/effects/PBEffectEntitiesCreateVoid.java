/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesCreateVoid extends PBEffectEntityBased
{
    public PBEffectEntitiesCreateVoid() {}

    public PBEffectEntitiesCreateVoid(int maxTicksAlive, double range)
    {
        super(maxTicksAlive, range);
    }

    @Override
    public void affectEntity(Level world, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        if (world instanceof ServerLevel)
        {
            if (entity instanceof Player)
            {
                int baseY = Mth.floor(entity.getY());
                int baseX = Mth.floor(entity.getX());
                int baseZ = Mth.floor(entity.getZ());

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
