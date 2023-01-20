/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

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
    public void doEffect(World world, PandorasBoxEntity entity, Random random, float newRatio, float prevRatio, double x, double y, double z)
    {
        if (world instanceof ServerWorld)
        {
            ServerWorld serverWorld = (ServerWorld) world;
            LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create(world);
            if(lightningBolt == null) return;
            lightningBolt.moveTo(Vector3d.atBottomCenterOf(new BlockPos(x, y, z)));
            serverWorld.addFreshEntity(lightningBolt);
        }
    }
}
