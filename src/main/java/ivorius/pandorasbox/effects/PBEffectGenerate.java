/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerate extends PBEffectRangeBased
{
    public int unifiedSeed;

    public PBEffectGenerate(int time, double range, int passes, int unifiedSeed)
    {
        super(time, range, passes);

        this.unifiedSeed = unifiedSeed;
    }

    @Override
    public void generateInRange(World world, EntityPandorasBox entity, Random random, Vec3d effectCenter, double prevRange, double newRange, int pass)
    {
        int requiredRange = MathHelper.ceil(newRange);

        int baseX = MathHelper.floor(effectCenter.x);
        int baseY = MathHelper.floor(effectCenter.y);
        int baseZ = MathHelper.floor(effectCenter.z);

        for (int x = -requiredRange; x <= requiredRange; x++)
        {
            for (int y = -requiredRange; y <= requiredRange; y++)
            {
                for (int z = -requiredRange; z <= requiredRange; z++)
                {
                    double dist = MathHelper.sqrt(x * x + y * y + z * z);

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

    public abstract void generateOnBlock(World world, EntityPandorasBox entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range);

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);
        compound.putInt("unifiedSeed", unifiedSeed);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);
        unifiedSeed = compound.getInt("unifiedSeed");
    }
}
