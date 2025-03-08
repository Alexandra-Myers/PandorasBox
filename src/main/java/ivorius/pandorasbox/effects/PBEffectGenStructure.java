package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
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
    public BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos();
    public boolean grounded;
    public BlockPos center = new BlockPos.MutableBlockPos();

    public PBEffectGenStructure(int maxTicksAlive, int maxX, int maxZ, int maxY, int startY, int unifiedSeed) {
        this(maxTicksAlive, maxX, maxZ, maxY, startY, unifiedSeed, true);
    }
    public PBEffectGenStructure(int maxTicksAlive, int maxX, int maxZ, int maxY, int startY, int unifiedSeed, boolean grounded) {
        super(maxTicksAlive);
        length = maxX;
        width = maxZ;
        height = maxY;
        startingYOffset = startY;
        this.grounded = grounded;
        this.unifiedSeed = unifiedSeed;
    }

    @Override
    public void setUpEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(effectCenter.x, effectCenter.y - startingYOffset, effectCenter.z);
        BlockState state = level.getBlockState(blockPos);
        if (grounded) {
            while (state.isAir()) {
                blockPos.move(0, -1, 0);
                state = level.getBlockState(blockPos);
            }
            while (!level.getBlockState(blockPos.above()).isAir()) {
                blockPos.move(0, 1, 0);
            }
        }
        center = blockPos.immutable();
        current = center.offset(-length, 0, -width).mutable();
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (level.isClientSide()) return;
        boolean bl = false;

        int i = 0;
        while (!bl) {
            i++;
            if (i >= 40)
                break;
            if (current.getY() <= center.getY() + height) {
                if (current.getX() <= center.getX() + length) {
                    if (current.getZ() <= center.getZ() + width) {
                        bl = buildStructure(level, entity, current, random, prevRatio, newRatio, length, width, height, center.getY(), center.getX(), center.getZ());
                        current.move(0, 0, 1);
                    } else {
                        current.set(current.getX() + 1, current.getY(), center.getZ() - width);
                    }
                } else {
                    current.set(center.getX() - length, current.getY() + 1, current.getZ());
                }
            } else break;
        }
    }
    public abstract boolean buildStructure(Level level, PandorasBoxEntity entity, BlockPos currentPos, RandomSource random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ);

}
