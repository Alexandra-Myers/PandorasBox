package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class PBEffectGenRuinedPortal extends PBEffectGenStructure {
    public WeightedBlock[] bricks = new WeightedBlock[]{};
    public ArrayListExtensions<RandomizedItemStack> loot = new ArrayListExtensions<>();
    public Direction.Axis axis = Direction.Axis.X;
    public boolean[] usedStairsForTop = new boolean[2];
    public PBEffectGenRuinedPortal() {}

    public PBEffectGenRuinedPortal(int time, int maxH, int maxY, int startY, int unifiedSeed, WeightedBlock[] brickSet, ArrayListExtensions<RandomizedItemStack> loot, Direction.Axis axis) {
        super(time, maxH, maxH, maxY, startY, unifiedSeed);

        this.bricks = brickSet;
        this.loot = loot;
        this.axis = axis;
    }
    @Override
    public boolean buildStructure(World world, PandorasBoxEntity entity, BlockPos currentPos, Random random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ) {
        height -= 1;
        int relativeHeight = currentPos.getY() - originY;
        double relative = (double) relativeHeight / height;
        int portalHAxis = Math.max(MathHelper.ceil(length * 0.5), 2);
        int currentH = currentPos.getX();
        int originH = originX;
        int currentOH = currentPos.getZ();
        int originOH = originZ;
        if (Objects.requireNonNull(axis) == Direction.Axis.Z) {
            currentH = currentPos.getZ();
            originH = originZ;
            currentOH = currentPos.getX();
            originOH = originX;
        }
        if (currentOH == originOH) {
            if ((currentPos.getY() == originY || relative == Math.ceil(relative)) && IvMathHelper.isBetweenInclusive(currentH, originH, portalHAxis)) {
                return portalEdges(world, currentPos, random);
            } else if (currentPos.getY() == originY + height + 1 && IvMathHelper.isBetweenInclusive(currentH, originH, portalHAxis + 1)) {
                return portalTop(world, currentPos, random, originX, originZ, currentH - originH);
            } else if (IvMathHelper.isBetweenInclusive(currentPos.getY(), MathHelper.floor(originY + height * 0.5), MathHelper.ceil(height * 0.5)) && IvMathHelper.compareOffsets(currentH, originH, portalHAxis)) {
                return portalEdges(world, currentPos, random);
            } else if (IvMathHelper.isBetweenInclusive(currentPos.getY(), MathHelper.floor(originY + height * 0.5), MathHelper.ceil(height * 0.5)) && IvMathHelper.compareOffsets(currentH, originH, portalHAxis + 1)) {
                if (random.nextDouble() > 0.25) {
                    Block block = WeightedSelector.selectItem(random, Arrays.asList(bricks)).block;
                    setBlockSafe(world, currentPos, block instanceof StairsBlock ? block.defaultBlockState().setValue(StairsBlock.HALF, random.nextBoolean() ? Half.TOP : Half.BOTTOM) : block instanceof SlabBlock ? block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.values()[world.random.nextInt(SlabType.values().length - 1)]) : block.defaultBlockState());
                    return true;
                }
            }
        } else if (currentPos.getY() == originY && IvMathHelper.isBetweenInclusive(currentH, originH, 2) && world.getBlockState(currentPos).getFluidState().is(FluidTags.LAVA)) {
            if (random.nextDouble() > 0.25) {
                setBlockSafe(world, currentPos, bricks[3].block.defaultBlockState());
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);
        compound.putString("axis", axis == Direction.Axis.X ? "x" : "z");
        PBNBTHelper.writeNBTRandomizedStacks("loot", loot.toArray(new RandomizedItemStack[]{}), compound);
        PBNBTHelper.writeNBTWeightedBlocks("bricks", bricks, compound);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);
        axis = compound.getString("axis").equals("x") ? Direction.Axis.X : Direction.Axis.Z;

        RandomizedItemStack[] randomizedItemStacks = PBNBTHelper.readNBTRandomizedStacks("loot", compound);
        loot = new ArrayListExtensions<>(randomizedItemStacks.length);
        Collections.addAll(loot, randomizedItemStacks);

        this.bricks = PBNBTHelper.readNBTWeightedBlocks("bricks", compound);
    }

    public static boolean portalEdges(World world, BlockPos currentPos, Random random) {
        if (random.nextDouble() > 0.25) {
            if (random.nextDouble() > 0.75)
                setBlockSafe(world, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
            else
                setBlockSafe(world, currentPos, Blocks.OBSIDIAN.defaultBlockState());
            return true;
        }
        return false;
    }
    public boolean portalTop(World world, BlockPos currentPos, Random random, int originX, int originZ, int diff) {
        if (currentPos.getX() == originX && currentPos.getZ() == originZ) {
            if (random.nextDouble() > 0.25) {
                setBlockSafe(world, currentPos, Blocks.GOLD_BLOCK.defaultBlockState());
                return true;
            }
        } else if (random.nextDouble() > 0.25) {
            boolean isNegative = diff < 0;
            Block block = WeightedSelector.selectItem(random, Arrays.asList(bricks)).block;
            BlockState state = block.defaultBlockState();
            if (block instanceof StairsBlock) {
                state = state.setValue(StairsBlock.FACING, Direction.fromAxisAndDirection(axis, isNegative ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE));
                if (isNegative ? usedStairsForTop[0] : usedStairsForTop[1])
                    state = bricks[3].block.defaultBlockState();
                if (isNegative)
                    usedStairsForTop[0] = true;
                else
                    usedStairsForTop[1] = true;
            } else if (!(block instanceof SlabBlock) && random.nextBoolean())
                state = bricks[3].block.defaultBlockState();
            setBlockSafe(world, currentPos, state);
            return true;
        }
        return false;
    }
}
