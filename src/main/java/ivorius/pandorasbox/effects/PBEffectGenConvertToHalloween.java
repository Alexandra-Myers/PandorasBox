/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHalloween extends PBEffectGenerate {
    public PBEffectGenConvertToHalloween() {}
    public PBEffectGenConvertToHalloween(int time, double range, int unifiedSeed) {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);

                if (Block.isShapeFullBlock(blockBelowState.getBlockSupportShape(world, posBelow)) && blockState.isAir(world, pos) && block != Blocks.WATER) {
                    if (random.nextInt(5 * 5) == 0) {
                        int b = world.random.nextInt(7);

                        if (b == 0) {
                            setBlockSafe(world, posBelow, Blocks.NETHERRACK.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                        } else if (b == 1) {
                            setBlockSafe(world, pos, Blocks.JACK_O_LANTERN.defaultBlockState());
                        } else if (b == 2) {
                            setBlockSafe(world, pos, Blocks.CARVED_PUMPKIN.defaultBlockState());
                        } else if (b == 3) {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.PUMPKIN_STEM.getStateDefinition().getPossibleStates().get(world.random.nextInt(4) + 4));
                        } else if (b == 4) {
                            setBlockSafe(world, pos, Blocks.CAKE.defaultBlockState());
                        } else if(b == 5) {
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            world.addFreshEntity(entityItem);
                        } else {
                            setBlockSafe(world, posBelow, Blocks.SOUL_SOIL.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                        }
                    }
                }
            } else {
                ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilySpawnEntity(world, entity, random, "zombified_piglin", 1.0f / (20 * 20), pos),
                        lazilySpawnEntity(world, entity, random, "enderman", 1.0f / (20 * 20), pos),
                        lazilySpawnEntity(world, entity, random, "phantom", 1.0f / (20 * 20), pos));
                for (Entity entity1 : entities) {
                    canSpawnEntity(world, blockState, pos, entity1);
                }
            }
        }
    }
}
