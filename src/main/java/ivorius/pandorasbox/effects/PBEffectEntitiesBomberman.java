/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBomberman extends PBEffectEntityBased
{
    public int bombs;

    public PBEffectEntitiesBomberman(int maxTicksAlive, double range, int bombs)
    {
        super(maxTicksAlive, range);
        this.bombs = bombs;
    }

    @Override
    public void affectEntity(World world, EntityPandorasBox box, Random random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        if (world instanceof ServerWorld)
        {
            int prevBombs = MathHelper.floor(prevRatio * bombs);
            int newBombs = MathHelper.floor(newRatio * bombs);
            int bombs = newBombs - prevBombs;

            for (int i = 0; i < bombs; i++)
            {
                TNTEntity entitytntprimed = new TNTEntity(world, entity.getX(), entity.getY(), entity.getZ(), null);
                entitytntprimed.setFuse(45 + random.nextInt(20));

                world.addFreshEntity(entitytntprimed);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putInt("bombs", bombs);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        bombs = compound.getInt("bombs");
    }
}
