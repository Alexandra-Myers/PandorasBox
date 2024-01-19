/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.weighted.WeightedSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by Alexandra on 2.10.23.
 */
public class PBEffectGenConvertToCity extends PBEffectGenerate {
    public List<EntityType<?>> mobsToSpawn;
    public PBEffectGenConvertToCity() {}

    public PBEffectGenConvertToCity(int time, double range, int unifiedSeed, List<EntityType<?>> mobs)
    {
        super(time, range, 2, unifiedSeed);
        mobsToSpawn = mobs;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
                blocks.addAll(Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);
                blocks.addAll(PandorasBox.flowers);
                ArrayListExtensions<Block> solid = new ArrayListExtensions<>();
                solid.addAll(PandorasBox.terracotta, PandorasBox.stained_terracotta);
                if (isBlockAnyOf(block, blocks)) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.SAND, Blocks.RED_SAND, Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DEEPSLATE, Blocks.TUFF)) {
                    if (world.getBlockState(pos.above()).getBlock() == Blocks.AIR) {
                        if (world.random.nextInt(144) == 0) {
                            for (int i = 0; i < 4; i++) {
                                BlockPos newPos;
                                switch (i) {
                                    case 0 -> {
                                        newPos = pos.east();
                                        setBlockSafe(world, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                    }
                                    case 1 -> {
                                        newPos = pos.west();
                                        setBlockSafe(world, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                    }
                                    case 2 -> {
                                        newPos = pos.north();
                                        setBlockSafe(world, newPos.east(), Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.west(), Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.north().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.north().east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.north().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.north().west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.north().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                    }
                                    case 3 -> {
                                        newPos = pos.south();
                                        setBlockSafe(world, newPos.east(), Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.west(), Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.south().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.south().east().east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.south().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.south().west().west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                        setBlockSafe(world, newPos.south().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                    }
                                }
                            }
                            setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                            setBlockSafe(world, pos.above(), Blocks.OAK_SAPLING.defaultBlockState().setValue(SaplingBlock.STAGE, 1));
                            ((SaplingBlock) Blocks.OAK_SAPLING).advanceTree(serverLevel, pos.above(),serverLevel.getBlockState(pos.above()), world.random);
                        } else if (world.random.nextInt(81) == 0) {
                            int pHeight = random.nextIntBetweenInclusive(3, 7);
                            for (int yp = 0; yp <= pHeight; yp++) {
                                if(yp != pHeight)
                                    setBlockSafe(world, pos.above(yp), Blocks.STONE_BRICK_WALL.defaultBlockState());
                                else {
                                    BlockPos newPos = pos.above(yp);
                                    setBlockSafe(world, newPos, Blocks.STONE_BRICK_WALL.defaultBlockState());
                                    newPos = randomDir(newPos, world.random);
                                    setBlockSafe(world, newPos, Blocks.STONE_BRICK_WALL.defaultBlockState());
                                    setBlockSafe(world, newPos.below(), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE));
                                }
                            }
                        } else if (world.random.nextInt(144) == 0) {
                            setBlockSafe(world, pos, Blocks.WHITE_CONCRETE.defaultBlockState());
                            int width = world.random.nextIntBetweenInclusive(3, 6);
                            int floors = world.random.nextIntBetweenInclusive(1, 10);
                            floors = floors < 3 || world.random.nextFloat() > 0.8 ? floors : 3;
                            int height = floors * width * 2;
                            for (int y = pos.getY(); y <= pos.getY() + height + 2; y++) {
                                for (int x = pos.getX() - width; x <= pos.getX() + width; x++) {
                                    for (int z = pos.getZ() - width; z <= pos.getZ() + width; z++) {
                                        buildStructure(world, new BlockPos(x, y, z), width, height, pos.getY(), pos.getX(), pos.getZ());
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
                                    setBlockSafe(world, stepPos, Blocks.POLISHED_ANDESITE.defaultBlockState());
                                    BlockPos heightenedPos = stepPos.immutable();
                                    for (int i = 0; i < 3; i++) {
                                        heightenedPos = heightenedPos.above();
                                        setBlockToAirSafe(world, heightenedPos);
                                    }
                                } else {
                                    if(stepPos.getY() - pos.getY() > 0)
                                        setBlockSafe(world, stepPos, inverseState);
                                    stepPos = stepPos.above();
                                    if (stepPos.getY() - pos.getY() < height + 1) {
                                        setBlockSafe(world, stepPos, stairState);
                                        BlockPos heightenedPos = stepPos.immutable();
                                        for (int i = 0; i < 3; i++) {
                                            heightenedPos = heightenedPos.above();
                                            setBlockToAirSafe(world, heightenedPos);
                                        }
                                    } else
                                        setBlockSafe(world, stepPos.below(), Blocks.POLISHED_ANDESITE.defaultBlockState());
                                }
                                stairPos = stepPos;
                            }
                        } else if (world.random.nextInt(64) == 0) {
                            setBlockSafe(world, pos, Blocks.GLASS.defaultBlockState());
                            setBlockSafe(world, pos.below(), Blocks.REDSTONE_LAMP.defaultBlockState());
                            setBlockSafe(world, pos.below(2), Blocks.REDSTONE_BLOCK.defaultBlockState());
                        } else
                            setBlockSafe(world, pos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
                    } else
                        setBlockSafe(world, pos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
                } else if (isBlockAnyOf(block, solid))
                    setBlockSafe(world, pos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
                else if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.LAVA, Blocks.ICE))
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());

                if (isBlockAnyOf(block, Blocks.LAVA))
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());

                if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE))
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            } else {
                Entity villager = lazilySpawnEntity(world, entity, random, "villager", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, villager);
            }
            changeBiome(Biomes.PLAINS, pass, effectCenter, serverLevel);
        }
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
                    int entity = world.random.nextInt(mobsToSpawn.size());
                    spawnerBlock.setEntityId(mobsToSpawn.get(entity), world.random);
                }
                return;
            }
            if ((IvMathHelper.compareOffsets(currentPos.getX(), originX, width - 1) && currentPos.getZ() == originZ)) {
                setBlockSafe(world, currentPos.above(), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.fromDelta(originX - currentPos.getX(), 0, 0)));
                ChestBlockEntity chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(currentPos.above());

                if (chestBlockEntity != null) {
                    Collection<WeightedSet> sets = PandorasBoxHelper.equipmentSets;
                    Collection<RandomizedItemStack> itemSelection = PandorasBoxHelper.items;
                    if (world.random.nextFloat() > 0.05) {
                        for (int i = 0; i < world.random.nextInt(5) + 2; i++) {
                            RandomizedItemStack chestContent = WeightedSelector.selectItem(world.random, itemSelection);
                            ItemStack stack = chestContent.itemStack.copy();
                            stack.setCount(chestContent.min + world.random.nextInt(chestContent.max - chestContent.min + 1));
                            int slot = world.random.nextInt(chestBlockEntity.getContainerSize());
                            while (!chestBlockEntity.getItem(slot).isEmpty())
                                slot = world.random.nextInt(chestBlockEntity.getContainerSize());

                            chestBlockEntity.setItem(slot, stack);
                        }
                    } else {
                        ItemStack[] itemSet = WeightedSelector.selectItem(world.random, sets).set;
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
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        PBNBTHelper.writeNBTEntities("entities", mobsToSpawn.toArray(new EntityType[0]), compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        mobsToSpawn = List.of(Objects.requireNonNull(PBNBTHelper.readNBTEntities("entities", compound)));
    }
}