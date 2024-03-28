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
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

/**
 * Created by Alexandra on 2.10.23.
 */
public class PBEffectGenConvertToCity extends PBEffectGenerate {
    public List<EntityType<?>> mobsToSpawn;
    public PBEffectGenConvertToCity() {}

    public PBEffectGenConvertToCity(int time, double range, int unifiedSeed, List<EntityType<?>> mobs) {
        super(time, range, 2, unifiedSeed);
        mobsToSpawn = mobs;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
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
                } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.SAND, Blocks.RED_SAND, Blocks.DIRT, Blocks.GRASS_BLOCK)) {
                    if (world.getBlockState(pos.above()).getBlock() == Blocks.AIR) {
                        if (world.random.nextInt(144) == 0) {
                            for (int i = 0; i < 4; i++) {
                                BlockPos newPos;
                                switch (i) {
                                    case 0: {
                                        newPos = pos.east();
                                        setBlockSafe(world, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.east().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                    }
                                    case 1: {
                                        newPos = pos.west();
                                        setBlockSafe(world, newPos, Blocks.GRASS_BLOCK.defaultBlockState());
                                        setBlockSafe(world, newPos.west().above(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                                    }
                                    case 2: {
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
                                    case 3: {
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
                            ((SaplingBlock) Blocks.OAK_SAPLING).advanceTree(serverWorld, pos.above(),serverWorld.getBlockState(pos.above()), world.random);
                        } else if (world.random.nextInt(81) == 0) {
                            int pHeight = random.nextInt(5) + 3;
                            for (int yp = 0; yp <= pHeight; yp++) {
                                if (yp != pHeight)
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
                            int width = world.random.nextInt(4) + 3;
                            int floors = world.random.nextInt(10) + 1;
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
                                BlockPos stepPos = stairPos.relative(direction);
                                BlockState stairState = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, direction);
                                BlockState inverseState = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, direction.getOpposite()).setValue(StairsBlock.HALF, Half.TOP);
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
        }
    }
    public void buildStructure(World world, BlockPos currentPos, int width, int height, int originY, int originX, int originZ) {
        ServerWorld serverWorld = (ServerWorld) world;
        int relativeHeight = currentPos.getY() - originY;
        double relative = relativeHeight / (double) (width * 2);
        if (currentPos.getY() == originY || relative == Math.ceil(relative)) {
            if(currentPos.getX() == originX && currentPos.getZ() == originZ) {
                setBlockSafe(serverWorld, currentPos, Blocks.SPAWNER.defaultBlockState());
                TileEntity block = world.getBlockEntity(currentPos);
                if (block instanceof MobSpawnerTileEntity) {
                    MobSpawnerTileEntity spawnerBlock = (MobSpawnerTileEntity) block;
                    int entity = world.random.nextInt(mobsToSpawn.size());
                    spawnerBlock.getSpawner().setEntityId(mobsToSpawn.get(entity));
                }
                return;
            }
            if ((IvMathHelper.compareOffsets(currentPos.getX(), originX, width - 1) && currentPos.getZ() == originZ)) {
                setBlockSafe(world, currentPos.above(), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.fromAxisAndDirection(Direction.Axis.X, originX - currentPos.getX() >= 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE)));
                ChestTileEntity chestTileEntity = (ChestTileEntity) world.getBlockEntity(currentPos.above());

                if (chestTileEntity != null) {
                    Collection<WeightedSet> sets = PandorasBoxHelper.equipmentSets;
                    Collection<RandomizedItemStack> itemSelection = PandorasBoxHelper.items;
                    if (world.random.nextFloat() > 0.05) {
                        for (int i = 0; i < world.random.nextInt(5) + 2; i++) {
                            RandomizedItemStack chestContent = WeightedSelector.selectItem(world.random, itemSelection);
                            ItemStack stack = chestContent.itemStack.copy();
                            stack.setCount(chestContent.min + world.random.nextInt(chestContent.max - chestContent.min + 1));
                            int slot = world.random.nextInt(chestTileEntity.getContainerSize());
                            while (!chestTileEntity.getItem(slot).isEmpty())
                                slot = world.random.nextInt(chestTileEntity.getContainerSize());

                            chestTileEntity.setItem(slot, stack);
                        }
                    } else {
                        ItemStack[] itemSet = WeightedSelector.selectItem(world.random, sets).set;
                        ItemStack[] chestContent = new ItemStack[itemSet.length];
                        for (int i = 0; i < itemSet.length; i++) {
                            chestContent[i] = itemSet[i].copy();
                        }
                        for (ItemStack stack : chestContent) {
                            int slot = world.random.nextInt(chestTileEntity.getContainerSize());
                            while (!chestTileEntity.getItem(slot).isEmpty())
                                slot = world.random.nextInt(chestTileEntity.getContainerSize());

                            chestTileEntity.setItem(slot, stack);
                        }
                    }
                }
            }
            if (IvMathHelper.compareOffsets(currentPos.getX(), originX, width - 1) || IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width - 1)) {
                setBlockSafe(world, currentPos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
                return;
            }
            setBlockSafe(serverWorld, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
        } else if (IvMathHelper.compareOffsets(currentPos.getX(), originX, width) || IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width)) {
            if (relativeHeight >= height + 1) {
                if (IvMathHelper.compareOffsets(currentPos.getX(), originX, width) && IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width))
                    setBlockSafe(serverWorld, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
                else if (relativeHeight == height + 1)
                    setBlockSafe(world, currentPos, glassState(serverWorld, currentPos, (PaneBlock) Blocks.CYAN_STAINED_GLASS_PANE));
                return;
            }
            if ((currentPos.getX() == originX || currentPos.getZ() == originZ)) {
                if (relativeHeight < 3)
                    setBlockToAirSafe(serverWorld, currentPos);
                else if (relativeHeight == 3)
                    setBlockSafe(serverWorld, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
                else
                    setBlockSafe(world, currentPos, glassState(serverWorld, currentPos, (PaneBlock) Blocks.CYAN_STAINED_GLASS_PANE));
            } else if ((currentPos.getX() == originX + width - 1 || currentPos.getZ() == originZ + width - 1))
                setBlockSafe(serverWorld, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
            else
                setBlockSafe(serverWorld, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
        } else if (!world.getBlockState(currentPos).is(Blocks.CHEST))
            setBlockToAirSafe(world, currentPos);

    }
    public BlockPos randomDir(BlockPos original, Random random) {
        Direction dir = Direction.from2DDataValue(random.nextInt(4));
        switch (dir) {
            case NORTH: {
                return original.north();
            }
            case SOUTH: {
                return original.south();
            }
            case EAST: {
                return original.east();
            }
            case WEST: {
                return original.west();
            }
            default: {
                return original;
            }
        }
    }
    public BlockState glassState(World world, BlockPos pos, PaneBlock block) {
        BlockPos north = pos.north();
        BlockPos south = pos.south();
        BlockPos west = pos.west();
        BlockPos east = pos.east();
        BlockState northState = world.getBlockState(north);
        BlockState southState = world.getBlockState(south);
        BlockState westState = world.getBlockState(west);
        BlockState eastState = world.getBlockState(east);
        return block.defaultBlockState()
                .setValue(PaneBlock.NORTH, block.attachsTo(northState, northState.isFaceSturdy(world, north, Direction.SOUTH)))
                .setValue(PaneBlock.SOUTH, block.attachsTo(southState, southState.isFaceSturdy(world, south, Direction.NORTH)))
                .setValue(PaneBlock.WEST, block.attachsTo(westState, westState.isFaceSturdy(world, west, Direction.EAST)))
                .setValue(PaneBlock.EAST, block.attachsTo(eastState, eastState.isFaceSturdy(world, east, Direction.WEST)));
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);
        PBNBTHelper.writeNBTEntities("entities", mobsToSpawn.toArray(new EntityType[0]), compound);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);
        mobsToSpawn = Arrays.asList(Objects.requireNonNull(PBNBTHelper.readNBTEntities("entities", compound)));
    }
}