/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBomberman extends PBEffectEntityBased
{
    public PBEffectEntitiesBomberman() {}
    public int bombs;

    public PBEffectEntitiesBomberman(int maxTicksAlive, double range, int bombs)
    {
        super(maxTicksAlive, range);
        this.bombs = bombs;
    }

    @Override
    public void affectEntity(Level world, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        if (world instanceof ServerLevel)
        {
            int prevBombs = Mth.floor(prevRatio * bombs);
            int newBombs = Mth.floor(newRatio * bombs);
            int bombs = newBombs - prevBombs;

            for (int i = 0; i < bombs; i++)
            {
                PrimedTnt entitytntprimed = new PrimedTnt(world, entity.getX(), entity.getY(), entity.getZ(), null);
                entitytntprimed.setFuse(45 + random.nextInt(20));

                world.addFreshEntity(entitytntprimed);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putInt("bombs", bombs);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        bombs = compound.getInt("bombs");
    }
}
