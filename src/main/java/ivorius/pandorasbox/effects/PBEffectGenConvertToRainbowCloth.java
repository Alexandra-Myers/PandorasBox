/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToRainbowCloth extends PBEffectGenerate
{
    public int[] woolMetas;
    public double ringSize;

    public PBEffectGenConvertToRainbowCloth(int time, double range, int unifiedSeed, int[] woolMetas, double ringSize)
    {
        super(time, range, 1, unifiedSeed);
        this.woolMetas = woolMetas;
        this.ringSize = ringSize;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        ArrayListExtensions<Block> wool = new ArrayListExtensions<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            if(BlockTags.WOOL.contains(block)) {
                wool.add(block);
            }
        }
        if (pass == 0)
        {
            if (world.loadedAndEntityCanStandOn(pos, entity))
            {
                if (world.getBlockState(pos.above()).isAir(world, pos.above()))
                {
                    double dist = MathHelper.sqrt(effectCenter.distanceToSqr(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)));
                    setBlockSafe(world, pos, wool.get(woolMetas[MathHelper.floor(dist / ringSize) % woolMetas.length]).defaultBlockState());
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putIntArray("woolMetas", woolMetas);
        compound.putDouble("ringSize", ringSize);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        woolMetas = compound.getIntArray("woolMetas");
        ringSize = compound.getDouble("ringSize");
    }
}
