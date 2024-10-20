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

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBombpack extends PBEffectEntityBased {
    public PBEffectEntitiesBombpack() {}

    public PBEffectEntitiesBombpack(int maxTicksAlive, double range)
    {
        super(maxTicksAlive, range);
    }

    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        Random itemRandom = new Random(entity.getId());
        double expectedBomb = itemRandom.nextDouble();
        if (newRatio >= expectedBomb && prevRatio < expectedBomb) {
            PrimedTnt entitytntprimed = new PrimedTnt(serverLevel, entity.getX(), entity.getY(), entity.getZ(), null);
            entitytntprimed.setFuse(60 + random.nextInt(160));

            serverLevel.addFreshEntity(entitytntprimed);
            entitytntprimed.startRiding(entity, true);
        }
    }
}
