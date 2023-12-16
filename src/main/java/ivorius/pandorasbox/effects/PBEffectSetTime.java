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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectSetTime extends PBEffectNormal
{
    public int totalPlus;
    public PBEffectSetTime() {}

    public PBEffectSetTime(int maxTicksAlive, int totalPlus)
    {
        super(maxTicksAlive);
        this.totalPlus = totalPlus;
    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio)
    {
        if (world.isClientSide()) return;
        int newPlus = Mth.floor(totalPlus * newRatio);
        int prevPlus = Mth.floor(totalPlus * prevRatio);
        int plus = newPlus - prevPlus;

        for(ServerLevel serverlevel : Objects.requireNonNull(world.getServer()).getAllLevels()) {
            serverlevel.setDayTime(world.getDayTime() + plus);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putInt("totalPlus", totalPlus);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        totalPlus = compound.getInt("totalPlus");
    }
}
