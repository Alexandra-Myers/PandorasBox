package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.utils.RandomizedItemTag;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.weighted.WeightedSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static ivorius.pandorasbox.effects.PBEffect.*;

public record CityMapper(Either<Block, TagKey<Block>>[] targets, List<EntityType<?>> spawnerEntities, List<WeightedSet> equipmentSets, EitherArrayList<RandomizedItemStack, RandomizedItemTag> items) implements BlockMapper {
    public static final MapCodec<CityMapper> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("targets").forGetter(CityMapper::targets),
                            BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().fieldOf("spawner_entities").forGetter(CityMapper::spawnerEntities),
                            WeightedSet.CODEC.listOf().fieldOf("equipment_sets").forGetter(CityMapper::equipmentSets),
                            RandomizedItemStack.LIST_CODEC.fieldOf("items").forGetter(CityMapper::items))
                    .apply(instance, CityMapper::new));

    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return targets.length == 0 || isBlockAnyOf(state.getBlock(), targets);
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        if (serverLevel.getBlockState(pos.above()).getBlock() == Blocks.AIR) {
            if (serverLevel.random.nextInt(144) == 0) {
                for (int i = 0; i < 4; i++) {
                    BlockPos newPos;
                    switch (i) {
                        case 0 -> {
                            newPos = pos.east();
                            setBlockSafe(serverLevel, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                        }
                        case 1 -> {
                            newPos = pos.west();
                            setBlockSafe(serverLevel, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                        }
                        case 2 -> {
                            newPos = pos.north();
                            setBlockSafe(serverLevel, newPos.east(), Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.west(), Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.north().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.north().east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.north().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.north().west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.north().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                        }
                        case 3 -> {
                            newPos = pos.south();
                            setBlockSafe(serverLevel, newPos.east(), Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.west(), Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.south().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.south().east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.south().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.south().west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            setBlockSafe(serverLevel, newPos.south().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                        }
                    }
                }
                setBlockSafe(serverLevel, pos, Blocks.DIRT.defaultBlockState());
                setBlockSafe(serverLevel, pos.above(), Blocks.OAK_SAPLING.defaultBlockState().setValue(SaplingBlock.STAGE, 1));
                ((SaplingBlock) Blocks.OAK_SAPLING).advanceTree(serverLevel, pos.above(),serverLevel.getBlockState(pos.above()), serverLevel.random);
            } else if (serverLevel.random.nextInt(81) == 0) {
                int pHeight = random.nextIntBetweenInclusive(3, 7);
                for (int yp = 0; yp <= pHeight; yp++) {
                    if(yp != pHeight)
                        setBlockSafe(serverLevel, pos.above(yp), Blocks.STONE_BRICK_WALL.defaultBlockState());
                    else {
                        BlockPos newPos = pos.above(yp);
                        setBlockSafe(serverLevel, newPos, Blocks.STONE_BRICK_WALL.defaultBlockState());
                        newPos = randomDir(newPos, serverLevel.random);
                        setBlockSafe(serverLevel, newPos, Blocks.STONE_BRICK_WALL.defaultBlockState());
                        setBlockSafe(serverLevel, newPos.below(), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE));
                    }
                }
            } else if (serverLevel.random.nextInt(144) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.WHITE_CONCRETE.defaultBlockState());
                int width = serverLevel.random.nextIntBetweenInclusive(3, 6);
                int floors = serverLevel.random.nextIntBetweenInclusive(1, 10);
                floors = floors < 3 || serverLevel.random.nextFloat() > 0.8 ? floors : 3;
                int height = floors * width * 2;
                for (int y = pos.getY(); y <= pos.getY() + height + 2; y++) {
                    for (int x = pos.getX() - width; x <= pos.getX() + width; x++) {
                        for (int z = pos.getZ() - width; z <= pos.getZ() + width; z++) {
                            buildStructure(serverLevel, new BlockPos(x, y, z), width, height, pos.getY(), pos.getX(), pos.getZ());
                        }
                    }
                }
                BlockPos stairPos = pos.offset(width - 1, 0, width - 1);
                Direction direction = Direction.WEST;
                int sideProgress = 0;
                while (stairPos.getY() - pos.getY() < height + 1) {
                    var stepPos = stairPos.relative(direction);
                    BlockState stairState = Blocks.STONE_BRICK_STAIRS.defaultBlockState().trySetValue(StairBlock.FACING, direction);
                    BlockState inverseState = Blocks.STONE_BRICK_STAIRS.defaultBlockState().trySetValue(StairBlock.FACING, direction.getOpposite()).trySetValue(StairBlock.HALF, Half.TOP);
                    if (++sideProgress == 2 * (width - 1)) {
                        direction = direction.getClockWise();
                        sideProgress = 0;
                        setBlockSafe(serverLevel, stepPos, Blocks.POLISHED_ANDESITE.defaultBlockState());
                        BlockPos heightenedPos = stepPos.immutable();
                        for (int i = 0; i < 3; i++) {
                            heightenedPos = heightenedPos.above();
                            setBlockToAirSafe(serverLevel, heightenedPos);
                        }
                    } else {
                        if(stepPos.getY() - pos.getY() > 0)
                            setBlockSafe(serverLevel, stepPos, inverseState);
                        stepPos = stepPos.above();
                        if (stepPos.getY() - pos.getY() < height + 1) {
                            setBlockSafe(serverLevel, stepPos, stairState);
                            BlockPos heightenedPos = stepPos.immutable();
                            for (int i = 0; i < 3; i++) {
                                heightenedPos = heightenedPos.above();
                                setBlockToAirSafe(serverLevel, heightenedPos);
                            }
                        } else
                            setBlockSafe(serverLevel, stepPos.below(), Blocks.POLISHED_ANDESITE.defaultBlockState());
                    }
                    stairPos = stepPos;
                }
            } else if (serverLevel.random.nextInt(64) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.GLASS.defaultBlockState());
                setBlockSafe(serverLevel, pos.below(), Blocks.REDSTONE_LAMP.defaultBlockState());
                setBlockSafe(serverLevel, pos.below(2), Blocks.REDSTONE_BLOCK.defaultBlockState());
            } else
                setBlockSafe(serverLevel, pos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
        } else
            setBlockSafe(serverLevel, pos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
    }
    public void buildStructure(Level world, BlockPos currentPos, int width, int height, int originY, int originX, int originZ) {
        ServerLevel serverLevel = (ServerLevel) world;
        int relativeHeight = currentPos.getY() - originY;
        double relative = relativeHeight / (double) (width * 2);
        if (currentPos.getY() == originY || relative == Math.ceil(relative)) {
            if(currentPos.getX() == originX && currentPos.getZ() == originZ) {
                setBlockSafe(serverLevel, currentPos, Blocks.SPAWNER.defaultBlockState());
                BlockEntity block = world.getBlockEntity(currentPos);
                if (block instanceof SpawnerBlockEntity spawnerBlock) {
                    int entity = world.random.nextInt(spawnerEntities.size());
                    spawnerBlock.setEntityId(spawnerEntities.get(entity), world.random);
                }
                return;
            }
            if ((IvMathHelper.compareOffsets(currentPos.getX(), originX, width - 1) && currentPos.getZ() == originZ)) {
                setBlockSafe(world, currentPos.above(), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.getApproximateNearest(originX - currentPos.getX(), 0, 0)));
                ChestBlockEntity chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(currentPos.above());

                if (chestBlockEntity != null) {
                    Collection<RandomizedItemStack> itemSelection = PandorasBoxHelper.assembleRandomisedStacks(BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK, items);
                    if (world.random.nextFloat() > 0.05) {
                        for (int i = 0; i < world.random.nextInt(5) + 2; i++) {
                            RandomizedItemStack chestContent = WeightedSelector.selectItem(world.random, itemSelection);
                            ItemStack stack = chestContent.itemStack().copy();
                            if (chestContent.max() > stack.getMaxStackSize()) stack.set(DataComponents.MAX_STACK_SIZE, chestContent.max());
                            stack.setCount(chestContent.min() + world.random.nextInt(chestContent.max() - chestContent.min() + 1));
                            int slot = world.random.nextInt(chestBlockEntity.getContainerSize());
                            while (!chestBlockEntity.getItem(slot).isEmpty())
                                slot = world.random.nextInt(chestBlockEntity.getContainerSize());

                            chestBlockEntity.setItem(slot, stack);
                        }
                    } else {
                        ItemStack[] itemSet = WeightedSelector.selectItem(world.random, equipmentSets).equipmentSet().set();
                        ItemStack[] chestContent = new ItemStack[itemSet.length];
                        for (int i = 0; i < itemSet.length; i++) {
                            chestContent[i] = itemSet[i].copy();
                        }
                        for (ItemStack stack : chestContent) {
                            int slot = world.random.nextInt(chestBlockEntity.getContainerSize());
                            while (!chestBlockEntity.getItem(slot).isEmpty())
                                slot = world.random.nextInt(chestBlockEntity.getContainerSize());

                            chestBlockEntity.setItem(slot, stack);
                        }
                    }
                }
            }
            if (IvMathHelper.compareOffsets(currentPos.getX(), originX, width - 1) || IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width - 1)) {
                setBlockSafe(world, currentPos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
                return;
            }
            setBlockSafe(serverLevel, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
        } else if (IvMathHelper.compareOffsets(currentPos.getX(), originX, width) || IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width)) {
            if (relativeHeight >= height + 1) {
                if (IvMathHelper.compareOffsets(currentPos.getX(), originX, width) && IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width))
                    setBlockSafe(serverLevel, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
                else if (relativeHeight == height + 1)
                    setBlockSafe(world, currentPos, glassState(serverLevel, currentPos, (IronBarsBlock) Blocks.CYAN_STAINED_GLASS_PANE));
                return;
            }
            if ((currentPos.getX() == originX || currentPos.getZ() == originZ)) {
                if (relativeHeight < 3)
                    setBlockToAirSafe(serverLevel, currentPos);
                else if (relativeHeight == 3)
                    setBlockSafe(serverLevel, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
                else
                    setBlockSafe(world, currentPos, glassState(serverLevel, currentPos, (IronBarsBlock) Blocks.CYAN_STAINED_GLASS_PANE));
            } else if ((currentPos.getX() == originX + width - 1 || currentPos.getZ() == originZ + width - 1))
                setBlockSafe(serverLevel, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
            else
                setBlockSafe(serverLevel, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
        } else if (!world.getBlockState(currentPos).is(Blocks.CHEST))
            setBlockToAirSafe(world, currentPos);

    }
    public BlockPos randomDir(BlockPos original, RandomSource random) {
        Direction dir = Direction.from2DDataValue(random.nextInt(4));
        return switch (dir) {
            case NORTH -> original.north();
            case SOUTH -> original.south();
            case EAST -> original.east();
            case WEST -> original.west();
            default -> original;
        };
    }
    public BlockState glassState(Level level, BlockPos pos, IronBarsBlock block) {
        BlockPos north = pos.north();
        BlockPos south = pos.south();
        BlockPos west = pos.west();
        BlockPos east = pos.east();
        BlockState northState = level.getBlockState(north);
        BlockState southState = level.getBlockState(south);
        BlockState westState = level.getBlockState(west);
        BlockState eastState = level.getBlockState(east);
        return block.defaultBlockState()
                .setValue(IronBarsBlock.NORTH, block.attachsTo(northState, northState.isFaceSturdy(level, north, Direction.SOUTH)))
                .setValue(IronBarsBlock.SOUTH, block.attachsTo(southState, southState.isFaceSturdy(level, south, Direction.NORTH)))
                .setValue(IronBarsBlock.WEST, block.attachsTo(westState, westState.isFaceSturdy(level, west, Direction.EAST)))
                .setValue(IronBarsBlock.EAST, block.attachsTo(eastState, eastState.isFaceSturdy(level, east, Direction.WEST)));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
