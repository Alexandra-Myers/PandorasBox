/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

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
    public PBEffectGenWorldSnake() {}

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
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio)
    {
        if (world instanceof ServerLevel)
        {
            int requiredRange = Mth.ceil(size);

            float f1 = Mth.cos(-dirYaw * 0.017453292F - (float) Math.PI);
            float f2 = Mth.sin(-dirYaw * 0.017453292F - (float) Math.PI);
            float f3 = -Mth.cos(-dirPitch * 0.017453292F);
            float f4 = Mth.sin(-dirPitch * 0.017453292F);

            double dirX = f2 * f3 * speed;
            double dirY = f4 * speed;
            double dirZ = f1 * f3 * speed;

            double newX = currentX + dirX;
            double newY = currentY + dirY;
            double newZ = currentZ + dirZ;

            int baseX = Mth.floor(newX);
            int baseY = Mth.floor(newY);
            int baseZ = Mth.floor(newZ);

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

            dirYawAcc += Mth.clamp((random.nextFloat() - random.nextFloat()) * 0.5f, -10.0f, 10.0f);
            dirPitchAcc += Mth.clamp((random.nextFloat() - random.nextFloat()) * 0.5f, -10.0f, 10.0f);
        }
    }

    public void generateOnBlock(Level world, PandorasBoxEntity entity, RandomSource random, BlockPos pos)
    {
        setBlockVarying(world, pos, blocks[random.nextInt(blocks.length)], unifiedSeed);
    }

    @Override
    public void writeToNBT(CompoundTag compound)
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
    public void readFromNBT(CompoundTag compound)
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
