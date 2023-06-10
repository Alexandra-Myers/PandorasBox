/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectRandomLightnings extends PBEffectPositionBased
{
    public PBEffectRandomLightnings() {}

    public PBEffectRandomLightnings(int time, int number, double range)
    {
        super(time, number, range);
    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, RandomSource random, float newRatio, float prevRatio, double x, double y, double z)
    {
        if (world instanceof ServerLevel serverWorld)
        {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(world);
            if(lightningBolt == null) return;
            lightningBolt.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
            serverWorld.addFreshEntity(lightningBolt);
        }
    }
}
