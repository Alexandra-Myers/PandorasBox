/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBombpack extends PBEffectEntityBased
{
    public PBEffectEntitiesBombpack() {}

    public PBEffectEntitiesBombpack(int maxTicksAlive, double range)
    {
        super(maxTicksAlive, range);
    }

    @Override
    public void affectEntity(Level world, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        if (world instanceof ServerLevel)
        {
            Random itemRandom = new Random(entity.getId());
            double expectedBomb = itemRandom.nextDouble();
            if (newRatio >= expectedBomb && prevRatio < expectedBomb)
            {
                PrimedTnt entitytntprimed = new PrimedTnt(world, entity.getX(), entity.getY(), entity.getZ(), null);
//                entitytntprimed.fuse = 60 + random.nextInt(160); // Use normal fuse for correct visual effect

                world.addFreshEntity(entitytntprimed);
                entitytntprimed.startRiding(entity, true);
            }
        }
    }
}
