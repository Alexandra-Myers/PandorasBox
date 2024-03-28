/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Objects;
import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectSetTime extends PBEffectNormal {
    public int totalPlus;
    public PBEffectSetTime() {}

    public PBEffectSetTime(int maxTicksAlive, int totalPlus) {
        super(maxTicksAlive);
        this.totalPlus = totalPlus;
    }

    @Override
    public void doEffect(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, float prevRatio, float newRatio) {
        if (world.isClientSide()) return;
        int newPlus = MathHelper.floor(totalPlus * newRatio);
        int prevPlus = MathHelper.floor(totalPlus * prevRatio);
        int plus = newPlus - prevPlus;

        for (ServerWorld serverWorld : Objects.requireNonNull(world.getServer()).getAllLevels()) {
            serverWorld.setDayTime(world.getDayTime() + plus);
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        compound.putInt("totalPlus", totalPlus);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        totalPlus = compound.getInt("totalPlus");
    }
}
