package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class PBEffectGenRuinedPortal extends PBEffectGenStructure {
    public ArrayListExtensions<WeightedBlock> bricks = new ArrayListExtensions<>();
    public ArrayListExtensions<RandomizedItemStack> loot = new ArrayListExtensions<>();
    public Direction.Axis axis = Direction.Axis.X;
    public PBEffectGenRuinedPortal() {}

    public PBEffectGenRuinedPortal(int time, int maxX, int maxZ, int maxY, int startY, int unifiedSeed, ArrayListExtensions<WeightedBlock> brickSet, ArrayListExtensions<RandomizedItemStack> loot, Direction.Axis axis)
    {
        super(time, maxX, maxZ, maxY, startY, unifiedSeed);

        this.bricks = brickSet;
        this.loot = loot;
        this.axis = axis;
    }
    @Override
    public void buildStructure(Level world, PandorasBoxEntity entity, BlockPos currentPos, RandomSource random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ) {
        boolean bl = Mth.ceil(length * 0.5) >= 2;
        boolean bl1 = Mth.ceil(width * 0.5) >= 2;
        int portalXAxis = bl ? Mth.ceil(length * 0.5) : 2;
        int portalZAxis = bl1 ? Mth.ceil(width * 0.5) : 2;
        if (currentPos.getY() >= startingYOffset + originY
                && currentPos.getY() < originY + height
                && axis == Direction.Axis.X
                && (IvMathHelper.compareOffsets(currentPos.getX(), originX, portalXAxis)
                    || ((currentPos.getY() == originY + startingYOffset || currentPos.getY() == originY + height - 1)
                    && (currentPos.getX() <= originX + portalXAxis && currentPos.getX() >= originX - portalXAxis)))) {
            if(random.nextDouble() > 0.25) {
                if(random.nextDouble() > 0.75) {
                    setBlockSafe(world, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
                } else {
                    setBlockSafe(world, currentPos, Blocks.OBSIDIAN.defaultBlockState());
                }
            }
        } else if (currentPos.getY() >= startingYOffset + originY
                && currentPos.getY() < originY + height
                && axis == Direction.Axis.Z
                && (IvMathHelper.compareOffsets(currentPos.getZ(), originZ, portalZAxis)
                    || ((currentPos.getY() == originY + startingYOffset || currentPos.getY() == originY + height - 1)
                    && (currentPos.getZ() <= originZ + portalZAxis && currentPos.getZ() >= originZ - portalZAxis)))) {
            if(random.nextDouble() > 0.25) {
                if(random.nextDouble() > 0.75) {
                    setBlockSafe(world, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
                } else {
                    setBlockSafe(world, currentPos, Blocks.OBSIDIAN.defaultBlockState());
                }
            }
        }
    }
}
