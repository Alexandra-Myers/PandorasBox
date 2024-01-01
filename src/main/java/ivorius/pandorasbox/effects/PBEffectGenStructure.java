package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class PBEffectGenStructure extends PBEffectNormal {
    public int length;
    public int width;
    public int height;
    public int startingYOffset;
    public int unifiedSeed;
    public int x;
    public int y;
    public int z;
    public boolean hasAlreadyStarted = false;
    public boolean grounded = true;
    public BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

    public PBEffectGenStructure() {
    }

    public PBEffectGenStructure(int maxTicksAlive, int maxX, int maxZ, int maxY, int startY, int unifiedSeed)
    {
        this(maxTicksAlive, maxX, maxZ, maxY, startY, unifiedSeed, true);
    }
    public PBEffectGenStructure(int maxTicksAlive, int maxX, int maxZ, int maxY, int startY, int unifiedSeed, boolean grounded)
    {
        super(maxTicksAlive);
        length = maxX;
        width = maxZ;
        height = maxY;
        startingYOffset = startY;
        this.grounded = grounded;
        this.unifiedSeed = unifiedSeed;
    }
    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if(world.isClientSide()) return;
        world.getBlockState(blockPos);
        if(!hasAlreadyStarted) {
            blockPos.set(effectCenter.x, effectCenter.y, effectCenter.z);
            BlockState state = world.getBlockState(blockPos);
            if(grounded) {
                while (state.isAir()) {
                    blockPos.move(0, -1, 0);
                    state = world.getBlockState(blockPos);
                }
                while (!world.getBlockState(blockPos.above()).isAir()) {
                    blockPos.move(0, 1, 0);
                }
            }
            blockPos.move(0, -startingYOffset, 0);
            x = blockPos.getX() - length;
            y = blockPos.getY();
            z = blockPos.getZ() - width;
            hasAlreadyStarted = true;
        }
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
    public abstract void buildStructure(Level world, PandorasBoxEntity entity, BlockPos currentPos, RandomSource random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ);
    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putInt("length", length);
        compound.putInt("width", width);
        compound.putInt("height", height);
        compound.putInt("x", x);
        compound.putInt("y", y);
        compound.putInt("z", z);
        compound.putBoolean("alreadyStarted", hasAlreadyStarted);
        compound.putBoolean("grounded", grounded);
        compound.putInt("startingYOffset", startingYOffset);
        compound.putInt("centerX", blockPos.getX());
        compound.putInt("centerY", blockPos.getY());
        compound.putInt("centerZ", blockPos.getZ());
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        length = compound.getInt("length");
        width = compound.getInt("width");
        height = compound.getInt("height");
        x = compound.getInt("x");
        y = compound.getInt("y");
        z = compound.getInt("z");
        blockPos.setX(compound.getInt("centerX"));
        blockPos.setY(compound.getInt("centerY"));
        blockPos.setZ(compound.getInt("centerZ"));
        hasAlreadyStarted = compound.getBoolean("alreadyStarted");
        grounded = compound.getBoolean("grounded");

        startingYOffset = compound.getInt("startingYOffset");
    }
}
