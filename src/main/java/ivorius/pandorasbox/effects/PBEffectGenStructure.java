package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public abstract class PBEffectGenStructure extends PBEffectNormal {
    public int length;
    public int width;
    public int height;
    public int startingYOffset;
    public int unifiedSeed;
    public Integer x = null;
    public Integer y = null;
    public Integer z = null;

    public PBEffectGenStructure() {
    }

    public PBEffectGenStructure(int maxTicksAlive, int maxX, int maxZ, int maxY, int startY, int unifiedSeed)
    {
        super(maxTicksAlive);
        length = maxX;
        width = maxZ;
        height = maxY;
        startingYOffset = startY;
        this.unifiedSeed = unifiedSeed;
    }
    @Override
    public void doEffect(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, float prevRatio, float newRatio) {
        BlockPos blockPos = new BlockPos(effectCenter);
        BlockState state = world.getBlockState(blockPos);
        while(state.isAir()) {
            blockPos = blockPos.below();
            state = world.getBlockState(blockPos);
        }
        blockPos = blockPos.below(startingYOffset);
        if(x == null) x = blockPos.getX() - length;
        if(y == null) y = blockPos.getY();
        if(z == null) z = blockPos.getZ() - width;
        if (y <= blockPos.getY() + height) {
            if (x <= blockPos.getX() + length) {
                if (z <= blockPos.getZ() + width) {
                    buildStructure(world, entity, new BlockPos(x, y, z), random, prevRatio, newRatio, length, width, height, blockPos.getY(), blockPos.getX(), blockPos.getZ());
                    z++;
                } else {
                    z = blockPos.getZ() - width;
                    x++;
                }
            } else {
                x = blockPos.getX() - length;
                y++;
            }
        }
    }
    public abstract void buildStructure(World world, PandorasBoxEntity entity, BlockPos currentPos, Random random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ);
    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putInt("length", length);
        compound.putInt("width", width);
        compound.putInt("height", height);
        if(!PBECRegistry.isAnyNull(x, y, z)) {
            compound.putInt("x", x);
            compound.putInt("y", y);
            compound.putInt("z", z);
        }
        compound.putInt("startingYOffset", startingYOffset);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        length = compound.getInt("length");
        width = compound.getInt("width");
        height = compound.getInt("height");
        x = compound.getInt("x");
        y = compound.getInt("y");
        z = compound.getInt("z");
        startingYOffset = compound.getInt("startingYOffset");
    }
}
