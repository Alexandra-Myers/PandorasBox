package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.atlas.atlascore.util.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

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
    public boolean buildStructure(Level level, PandorasBoxEntity entity, BlockPos currentPos, RandomSource random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ) {
        height -= 1;
        int relativeHeight = currentPos.getY() - originY;
        double relative = (double) relativeHeight / height;
        int portalHAxis = Math.max(Mth.ceil(length * 0.5), 2);
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
                return portalEdges(level, currentPos, random);
            } else if (currentPos.getY() == originY + height + 1 && IvMathHelper.isBetweenInclusive(currentH, originH, portalHAxis + 1)) {
                return portalTop(level, currentPos, random, originX, originZ, currentH - originH);
            } else if (IvMathHelper.isBetweenInclusive(currentPos.getY(), Mth.floor(originY + height * 0.5), Mth.ceil(height * 0.5)) && IvMathHelper.compareOffsets(currentH, originH, portalHAxis)) {
                return portalEdges(level, currentPos, random);
            } else if (IvMathHelper.isBetweenInclusive(currentPos.getY(), Mth.floor(originY + height * 0.5), Mth.ceil(height * 0.5)) && IvMathHelper.compareOffsets(currentH, originH, portalHAxis + 1)) {
                if (random.nextDouble() > 0.25) {
                    Block block = WeightedSelector.selectItem(random, Arrays.asList(bricks)).block;
                    setBlockSafe(level, currentPos, block instanceof StairBlock ? block.defaultBlockState().setValue(StairBlock.HALF, random.nextBoolean() ? Half.TOP : Half.BOTTOM) : block instanceof SlabBlock ? block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.values()[level.random.nextInt(SlabType.values().length - 1)]) : block.defaultBlockState());
                    return true;
                }
            }
        } else if (currentPos.getY() == originY && IvMathHelper.isBetweenInclusive(currentH, originH, 2) && level.getBlockState(currentPos).getFluidState().is(FluidTags.LAVA)) {
            if (random.nextDouble() > 0.25) {
                setBlockSafe(level, currentPos, bricks[3].block.defaultBlockState());
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);
        compound.putString("axis", axis == Direction.Axis.X ? "x" : "z");
        PBNBTHelper.writeNBTRandomizedStacks("loot", loot.toArray(new RandomizedItemStack[]{}), compound, registryAccess);
        PBNBTHelper.writeNBTWeightedBlocks("bricks", bricks, compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);
        axis = compound.getString("axis").equals("x") ? Direction.Axis.X : Direction.Axis.Z;

        RandomizedItemStack[] randomizedItemStacks = PBNBTHelper.readNBTRandomizedStacks("loot", compound, registryAccess);
        loot = new ArrayListExtensions<>(randomizedItemStacks.length);
        Collections.addAll(loot, randomizedItemStacks);

        this.bricks = PBNBTHelper.readNBTWeightedBlocks("bricks", compound);
    }

    public static boolean portalEdges(Level level, BlockPos currentPos, RandomSource random) {
        if (random.nextDouble() > 0.25) {
            if (random.nextDouble() > 0.75)
                setBlockSafe(level, currentPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
            else
                setBlockSafe(level, currentPos, Blocks.OBSIDIAN.defaultBlockState());
            return true;
        }
        return false;
    }
    public boolean portalTop(Level level, BlockPos currentPos, RandomSource random, int originX, int originZ, int diff) {
        if (currentPos.getX() == originX && currentPos.getZ() == originZ) {
            if (random.nextDouble() > 0.25) {
                setBlockSafe(level, currentPos, Blocks.GOLD_BLOCK.defaultBlockState());
                return true;
            }
        } else if (random.nextDouble() > 0.25) {
            boolean isNegative = diff < 0;
            Block block = WeightedSelector.selectItem(random, Arrays.asList(bricks)).block;
            BlockState state = block.defaultBlockState();
            if (block instanceof StairBlock) {
                state = state.setValue(StairBlock.FACING, Direction.fromAxisAndDirection(axis, isNegative ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE));
                if (isNegative ? usedStairsForTop[0] : usedStairsForTop[1])
                    state = bricks[3].block.defaultBlockState();
                if (isNegative)
                    usedStairsForTop[0] = true;
                else
                    usedStairsForTop[1] = true;
            } else if (!(block instanceof SlabBlock) && random.nextBoolean())
                state = bricks[3].block.defaultBlockState();
            setBlockSafe(level, currentPos, state);
            return true;
        }
        return false;
    }
}