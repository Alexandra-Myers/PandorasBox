/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerate2D extends PBEffectRangeBased
{
    public int unifiedSeed;
    public PBEffectGenerate2D() {}

    public PBEffectGenerate2D(int time, double range, int passes, int unifiedSeed)
    {
        super(time, range, passes);

        this.unifiedSeed = unifiedSeed;
    }

    @Override
    public void generateInRange(Level level, PandorasBoxEntity entity, RandomSource random, Vec3 effectCenter, double prevRange, double newRange, int pass)
    {
        int requiredRange = Mth.floor(newRange);

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        for (int x = -requiredRange; x <= requiredRange; x++)
        {
            for (int z = -requiredRange; z <= requiredRange; z++)
            {
                double dist = Mth.sqrt(x * x + z * z);

                if (dist <= newRange)
                {
                    if (dist > prevRange)
                    {
                        generateOnSurface(level, entity, effectCenter, random, new BlockPos(baseX + x, baseY, baseZ + z), dist, pass);
                    }
                    else
                    {
                        z = -z; // We can skip all blocks in between
                    }
                }
            }
        }
    }

    public abstract void generateOnSurface(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, BlockPos pos, double distance, int pass);

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess)
    {
        super.writeToNBT(compound, registryAccess);

        compound.putInt("unifiedSeed", unifiedSeed);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess)
    {
        super.readFromNBT(compound, registryAccess);

        unifiedSeed = compound.getInt("unifiedSeed");
    }
}
