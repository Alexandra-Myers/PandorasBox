/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerate extends PBEffectRangeBased
{
    public int unifiedSeed;
    public PBEffectGenerate() {}

    public PBEffectGenerate(int time, double range, int passes, int unifiedSeed)
    {
        super(time, range, passes);

        this.unifiedSeed = unifiedSeed;
    }

    @Override
    public void generateInRange(Level world, PandorasBoxEntity entity, RandomSource random, Vec3d effectCenter, double prevRange, double newRange, int pass)
    {
        int requiredRange = Mth.ceil(newRange);

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        for (int x = -requiredRange; x <= requiredRange; x++)
        {
            for (int y = -requiredRange; y <= requiredRange; y++)
            {
                for (int z = -requiredRange; z <= requiredRange; z++)
                {
                    double dist = Mth.sqrt(x * x + y * y + z * z);

                    if (dist <= newRange)
                    {
                        if (dist > prevRange)
                            generateOnBlock(world, entity, effectCenter, random, pass, new BlockPos(baseX + x, baseY + y, baseZ + z), dist);
                        else
                            z = -z; // We can skip all blocks in between
                    }
                }
            }
        }
    }

    public abstract void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range);

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);
        compound.putInt("unifiedSeed", unifiedSeed);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);
        unifiedSeed = compound.getInt("unifiedSeed");
    }
}
