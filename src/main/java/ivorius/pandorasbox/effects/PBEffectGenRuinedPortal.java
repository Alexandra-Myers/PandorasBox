package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;

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
        int relativeHeight = currentPos.getY() - originY;
        double relative = (double) relativeHeight / height;
        boolean bl = Mth.ceil(length * 0.5) >= 2;
        boolean bl1 = Mth.ceil(width * 0.5) >= 2;
        int portalXAxis = bl ? Mth.ceil(length * 0.5) : 2;
        int portalZAxis = bl1 ? Mth.ceil(width * 0.5) : 2;
        if ((currentPos.getY() == originY || relative == Math.ceil(relative)) && IvMathHelper.isBetween(currentPos.getX(), originX, portalXAxis) && axis == Direction.Axis.X  && currentPos.getZ() == originZ) {
            if (random.nextDouble() > 0.15) {
                if(random.nextDouble() > 0.75) {
                    setBlockSafe(world, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
                } else {
                    setBlockSafe(world, currentPos, Blocks.OBSIDIAN.defaultBlockState());
                }
            } else
                setBlockToAirSafe(world, currentPos);
        } else if ((currentPos.getY() == originY || relative == Math.ceil(relative)) && IvMathHelper.isBetween(currentPos.getZ(), originZ, portalZAxis) && axis == Direction.Axis.Z  && currentPos.getX() == originX) {
            if (random.nextDouble() > 0.15) {
                if(random.nextDouble() > 0.75) {
                    setBlockSafe(world, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
                } else {
                    setBlockSafe(world, currentPos, Blocks.OBSIDIAN.defaultBlockState());
                }
            } else
                setBlockToAirSafe(world, currentPos);
        } else if (IvMathHelper.isBetween(currentPos.getY(), Mth.floor(originY + height * 0.5), Mth.ceil(height * 0.5)) && IvMathHelper.compareOffsets(currentPos.getX(), originX, portalXAxis) && axis == Direction.Axis.X  && currentPos.getZ() == originZ) {
            if (random.nextDouble() > 0.15) {
                if(random.nextDouble() > 0.75) {
                    setBlockSafe(world, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
                } else {
                    setBlockSafe(world, currentPos, Blocks.OBSIDIAN.defaultBlockState());
                }
            } else
                setBlockToAirSafe(world, currentPos);
        } else if (IvMathHelper.isBetween(currentPos.getY(), Mth.floor(originY + height * 0.5), Mth.ceil(height * 0.5)) && IvMathHelper.compareOffsets(currentPos.getZ(), originZ, portalZAxis) && axis == Direction.Axis.Z  && currentPos.getX() == originX) {
            if (random.nextDouble() > 0.15) {
                if(random.nextDouble() > 0.75) {
                    setBlockSafe(world, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
                } else {
                    setBlockSafe(world, currentPos, Blocks.OBSIDIAN.defaultBlockState());
                }
            } else
                setBlockToAirSafe(world, currentPos);
        } else
            setBlockToAirSafe(world, currentPos);
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        compound.putString("axis", axis == Direction.Axis.X ? "x" : "z");
        PBNBTHelper.writeNBTRandomizedStacks("loot", loot.toArray(new RandomizedItemStack[]{}), compound);
        PBNBTHelper.writeNBTWeightedBlocks("bricks", bricks.toArray(new WeightedBlock[]{}), compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        axis = compound.getString("axis").equals("x") ? Direction.Axis.X : Direction.Axis.Z;

        RandomizedItemStack[] randomizedItemStacks = PBNBTHelper.readNBTRandomizedStacks("loot", compound);
        loot = new ArrayListExtensions<>(randomizedItemStacks.length);
        Collections.addAll(loot, randomizedItemStacks);

        WeightedBlock[] bricks = PBNBTHelper.readNBTWeightedBlocks("bricks", compound);
        this.bricks = new ArrayListExtensions<>(bricks.length);
        Collections.addAll(this.bricks, bricks);
    }
}
