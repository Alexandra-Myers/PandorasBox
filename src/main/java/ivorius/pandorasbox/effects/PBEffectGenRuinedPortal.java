package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PBEffectGenRuinedPortal extends PBEffectGenStructure {
    public static final MapCodec<PBEffectGenRuinedPortal> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.INT.fieldOf("max_horizontal").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.length),
                            Codec.INT.fieldOf("max_vertical").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.height),
                            Codec.INT.fieldOf("starting_y").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.startingYOffset),
                            Codec.INT.fieldOf("unified_seed").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.unifiedSeed),
                            Codec.BOOL.fieldOf("grounded").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.grounded),
                            BlockPos.CODEC.fieldOf("center").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.center),
                            BlockPos.CODEC.xmap(BlockPos::mutable, Function.identity()).fieldOf("current").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.current),
                            PBNBTHelper.arrayCodec(WeightedBlock.BLOCK_CODEC, () -> new WeightedBlock[0]).fieldOf("bricks").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.bricks),
                            RandomizedItemStack.CODEC.listOf().fieldOf("loot").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.loot),
                            Direction.Axis.CODEC.fieldOf("axis").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.axis),
                            PBNBTHelper.arrayCodec(Codec.BOOL, () -> new Boolean[0]).fieldOf("used_stairs").forGetter(pbEffectGenRuinedPortal -> pbEffectGenRuinedPortal.usedStairsForTop))
                    .apply(instance, PBEffectGenRuinedPortal::new));
    public WeightedBlock[] bricks;
    public List<RandomizedItemStack> loot;
    public Direction.Axis axis;
    public Boolean[] usedStairsForTop = new Boolean[] {false, false};

    public PBEffectGenRuinedPortal(int time, int maxH, int maxY, int startY, int unifiedSeed, WeightedBlock[] brickSet, List<RandomizedItemStack> loot, Direction.Axis axis) {
        super(time, maxH, maxH, maxY, startY, unifiedSeed);

        this.bricks = brickSet;
        this.loot = loot;
        this.axis = axis;
    }
    private PBEffectGenRuinedPortal(int time, int maxH, int maxY, int startY, int unifiedSeed, boolean grounded, BlockPos center, BlockPos.MutableBlockPos current, WeightedBlock[] brickSet, List<RandomizedItemStack> loot, Direction.Axis axis, Boolean[] usedStairsForTop) {
        super(time, maxH, maxH, maxY, startY, unifiedSeed, grounded);

        this.center = center;
        this.current = current;
        this.bricks = brickSet;
        this.loot = loot;
        this.axis = axis;
        this.usedStairsForTop = usedStairsForTop;
    }
    @Override
    public boolean buildStructure(Level level, PandorasBoxEntity entity, BlockPos currentPos, RandomSource random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ) {
        height--;
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
                    Block block = WeightedSelector.selectItem(random, Arrays.asList(bricks)).block().value();
                    setBlockSafe(level, currentPos, block instanceof StairBlock ? block.defaultBlockState().setValue(StairBlock.HALF, random.nextBoolean() ? Half.TOP : Half.BOTTOM) : block instanceof SlabBlock ? block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.values()[level.random.nextInt(SlabType.values().length - 1)]) : block.defaultBlockState());
                    return true;
                }
            }
        } else if (currentPos.getY() == originY && IvMathHelper.isBetweenInclusive(currentH, originH, 2) && level.getBlockState(currentPos).getFluidState().is(FluidTags.LAVA)) {
            if (random.nextDouble() > 0.25) {
                setBlockSafe(level, currentPos, bricks[3].block().value().defaultBlockState());
                return true;
            }
        }
        return false;
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
            Block block = WeightedSelector.selectItem(random, Arrays.asList(bricks)).block().value();
            BlockState state = block.defaultBlockState();
            if (block instanceof StairBlock) {
                state = state.setValue(StairBlock.FACING, Direction.fromAxisAndDirection(axis, isNegative ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE));
                if (isNegative ? usedStairsForTop[0] : usedStairsForTop[1])
                    state = bricks[3].block().value().defaultBlockState();
                if (isNegative)
                    usedStairsForTop[0] = true;
                else
                    usedStairsForTop[1] = true;
            } else if (!(block instanceof SlabBlock) && random.nextBoolean())
                state = bricks[3].block().value().defaultBlockState();
            setBlockSafe(level, currentPos, state);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}