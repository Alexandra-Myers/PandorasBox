/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToCity extends PBEffectGenerate
{
    public PBEffectGenConvertToCity() {}

    public PBEffectGenConvertToCity(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
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
                if (isBlockAnyOf(block, blocks)) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.SAND, Blocks.RED_SAND, Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DEEPSLATE, Blocks.TUFF)) {
                    if (world.getBlockState(pos.above()).getBlock() == Blocks.AIR) {
                        if (world.random.nextInt(6 * 6) == 0) {
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
                        } else if (world.random.nextInt(6 * 6) == 0) {
                            int pHeight = random.nextInt(5) + 3;
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
                        } else if (world.random.nextInt(2 * 2) == 0) {
                            setBlockSafe(world, pos, Blocks.WHITE_CONCRETE.defaultBlockState());
                            int x = 0, y = 0, z = 0;
                            for (int i = 0; i < world.random.nextIntBetweenInclusive(10, 5); i++) {
                                if (y <= pos.getY() + i) {
                                    if (x <= pos.getX() + i) {
                                        if (z <= pos.getZ() + i) {
                                            buildStructure(world, new BlockPos(x, y, z), i, pos.getY(), pos.getX(), pos.getZ());
                                            z++;
                                        } else {
                                            z = pos.getZ() - i;
                                            x++;
                                        }
                                    } else {
                                        x = pos.getX() - i;
                                        y++;
                                    }
                                }
                            }
                        } else if (world.random.nextInt(8 * 8) == 0) {
                            setBlockSafe(world, pos, Blocks.GLASS.defaultBlockState());
                            setBlockSafe(world, pos.below(), Blocks.REDSTONE_LAMP.defaultBlockState());
                            setBlockSafe(world, pos.below(2), Blocks.REDSTONE_BLOCK.defaultBlockState());
                        } else
                            setBlockSafe(world, pos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.CYAN_TERRACOTTA.defaultBlockState());
                    }
                } else if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.LAVA, Blocks.ICE)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }

                if (isBlockAnyOf(block, Blocks.LAVA)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }

                if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
            } else {
                Entity villager = lazilySpawnEntity(world, entity, random, "villager", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, villager);
            }
            changeBiome(Biomes.PLAINS, pass, effectCenter, serverLevel);
        }
    }
    public void buildStructure(Level world, BlockPos currentPos, int length, int originY, int originX, int originZ) {
        ServerLevel serverLevel = (ServerLevel) world;
        if (currentPos.getY() == originY || currentPos.getY() == originY + length * 2) {
            setBlockSafe(serverLevel, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
        } else if (IvMathHelper.compareOffsets(currentPos.getX(), originX, length) || IvMathHelper.compareOffsets(currentPos.getZ(), originZ, length)) {
            if (world.random.nextInt(8) != 0)
                setBlockSafe(serverLevel, currentPos, Blocks.WHITE_CONCRETE.defaultBlockState());
            else
                setBlockSafe(serverLevel, currentPos, glassState(serverLevel, currentPos));
        } else if (currentPos.getY() < originY + length) {
            setBlockToAirSafe(world, currentPos);
        } else {
            setBlockToAirSafe(world, currentPos);
        }
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
    public BlockState glassState(Level blockgetter, BlockPos blockpos) {
        IronBarsBlock block = (IronBarsBlock) Blocks.GLASS_PANE;
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.south();
        BlockPos blockpos3 = blockpos.west();
        BlockPos blockpos4 = blockpos.east();
        BlockState blockstate = blockgetter.getBlockState(blockpos1);
        BlockState blockstate1 = blockgetter.getBlockState(blockpos2);
        BlockState blockstate2 = blockgetter.getBlockState(blockpos3);
        BlockState blockstate3 = blockgetter.getBlockState(blockpos4);
        return block.defaultBlockState()
                .setValue(IronBarsBlock.NORTH, block.attachsTo(blockstate, blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH)))
                .setValue(IronBarsBlock.SOUTH, block.attachsTo(blockstate1, blockstate1.isFaceSturdy(blockgetter, blockpos2, Direction.NORTH)))
                .setValue(IronBarsBlock.WEST, block.attachsTo(blockstate2, blockstate2.isFaceSturdy(blockgetter, blockpos3, Direction.EAST)))
                .setValue(IronBarsBlock.EAST, block.attachsTo(blockstate3, blockstate3.isFaceSturdy(blockgetter, blockpos4, Direction.WEST)));
    }
}