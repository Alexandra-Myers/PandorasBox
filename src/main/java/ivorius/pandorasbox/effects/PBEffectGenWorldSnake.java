/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenWorldSnake extends PBEffectNormal
{
    public Block[] blocks;
    public int unifiedSeed;

    public double currentX;
    public double currentY;
    public double currentZ;

    public double size;

    public double speed;
    public float dirYaw;
    public float dirPitch;
    public float dirYawAcc;
    public float dirPitchAcc;

    public PBEffectGenWorldSnake(int maxTicksAlive, Block[] blocks, int unifiedSeed, double currentX, double currentY, double currentZ, double size, double speed, float dirYaw, float dirPitch)
    {
        super(maxTicksAlive);
        this.blocks = blocks;
        this.unifiedSeed = unifiedSeed;
        this.currentX = currentX;
        this.currentY = currentY;
        this.currentZ = currentZ;
        this.size = size;
        this.speed = speed;
        this.dirYaw = dirYaw;
        this.dirPitch = dirPitch;
    }

    @Override
    public void doEffect(World world, EntityPandorasBox entity, Vec3d effectCenter, Random random, float prevRatio, float newRatio)
    {
        if (world instanceof ServerWorld)
        {
            int requiredRange = MathHelper.ceil(size);

            float f1 = MathHelper.cos(-dirYaw * 0.017453292F - (float) Math.PI);
            float f2 = MathHelper.sin(-dirYaw * 0.017453292F - (float) Math.PI);
            float f3 = -MathHelper.cos(-dirPitch * 0.017453292F);
            float f4 = MathHelper.sin(-dirPitch * 0.017453292F);

            double dirX = f2 * f3 * speed;
            double dirY = f4 * speed;
            double dirZ = f1 * f3 * speed;

            double newX = currentX + dirX;
            double newY = currentY + dirY;
            double newZ = currentZ + dirZ;

            int baseX = MathHelper.floor(newX);
            int baseY = MathHelper.floor(newY);
            int baseZ = MathHelper.floor(newZ);

            for (int x = -requiredRange; x <= requiredRange; x++)
            {
                for (int y = -requiredRange; y <= requiredRange; y++)
                {
                    for (int z = -requiredRange; z <= requiredRange; z++)
                    {
                        if (PBEffectGenDome.isSpherePart(baseX + x + 0.5, baseY + y + 0.5, baseZ + z + 0.5, newX, newY, newZ, 0.0, size))
                        {
                            if (!PBEffectGenDome.isSpherePart(baseX + x + 0.5, baseY + y + 0.5, baseZ + z + 0.5, currentX, currentY, currentZ, 0.0, size))
                            {
                                generateOnBlock(world, entity, random, new BlockPos(x + baseX, y + baseY, z + baseZ));
                            }
                        }
                    }
                }
            }

            currentX = newX;
            currentY = newY;
            currentZ = newZ;

            dirYaw += dirYawAcc;
            dirPitch += dirPitchAcc;

            dirYawAcc += MathHelper.clamp((random.nextFloat() - random.nextFloat()) * 0.5f, -10.0f, 10.0f);
            dirPitchAcc += MathHelper.clamp((random.nextFloat() - random.nextFloat()) * 0.5f, -10.0f, 10.0f);
        }
    }

    public void generateOnBlock(World world, EntityPandorasBox entity, Random random, BlockPos pos)
    {
        setBlockVarying(world, pos, blocks[random.nextInt(blocks.length)], unifiedSeed);
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTBlocks("block", blocks, compound);
        compound.putInt("unifiedSeed", unifiedSeed);

        compound.putDouble("currentX", currentX);
        compound.putDouble("currentY", currentY);
        compound.putDouble("currentZ", currentZ);

        compound.putDouble("size", size);

        compound.putDouble("speed", speed);
        compound.putFloat("dirYaw", dirYaw);
        compound.putFloat("dirPitch", dirPitch);
        compound.putFloat("dirYawAcc", dirYawAcc);
        compound.putFloat("dirPitchAcc", dirPitchAcc);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        blocks = PBNBTHelper.readNBTBlocks("block", compound);
        unifiedSeed = compound.getInt("unifiedSeed");

        currentX = compound.getDouble("currentX");
        currentY = compound.getDouble("currentY");
        currentZ = compound.getDouble("currentZ");

        size = compound.getDouble("size");

        speed = compound.getDouble("speed");
        dirYaw = compound.getFloat("dirYaw");
        dirPitch = compound.getFloat("dirPitch");
        dirYawAcc = compound.getFloat("dirYawAcc");
        dirPitchAcc = compound.getFloat("dirPitchAcc");
    }
}
