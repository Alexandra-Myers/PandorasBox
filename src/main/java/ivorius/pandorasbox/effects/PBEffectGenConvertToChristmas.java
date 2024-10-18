/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToChristmas extends PBEffectGenerate {
    public PBEffectGenConvertToChristmas() {}
    public PBEffectGenConvertToChristmas(int time, double range, int unifiedSeed) {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Blocks.WATER)) {
                    setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
                } else if (blockState.isAir()) {
                    boolean setSnow = true;

                    BlockPos posBelow = pos.below();
                    if (world.loadedAndEntityCanStandOn(posBelow, entity)) {
                        if (random.nextInt(100) == 0) {
                            setBlockSafe(world, pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(world.random)));
                            ChestBlockEntity chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(pos);

                            if (chestBlockEntity != null) {
                                Collection<RandomizedItemStack> itemSelection = PandorasBoxHelper.blocksAndItems;
                                RandomizedItemStack chestContent = WeightedSelector.selectItem(random, itemSelection);
                                ItemStack stack = chestContent.itemStack.copy();
                                stack.setCount(chestContent.min + random.nextInt(chestContent.max - chestContent.min + 1));

                                chestBlockEntity.setItem(world.random.nextInt(chestBlockEntity.getContainerSize()), stack);
                            }

                            setSnow = false;
                        } else if (random.nextInt(100) == 0) {
                            setBlockSafe(world, pos, Blocks.REDSTONE_LAMP.defaultBlockState());
                            setBlockSafe(world, posBelow, Blocks.REDSTONE_BLOCK.defaultBlockState());
                            setSnow = false;
                        } else if (random.nextInt(100) == 0) {
                            setBlockSafe(world, pos, Blocks.CAKE.defaultBlockState());
                            setSnow = false;
                        } else if (random.nextInt(100) == 0) {
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            world.addFreshEntity(entityItem);
                        }
                    }

                    if (setSnow && Blocks.SNOW.defaultBlockState().canSurvive(world, pos)) {
                        setBlockSafe(world, pos, Blocks.SNOW.defaultBlockState());
                    }
                } else if (block == Blocks.FIRE || block == Blocks.SOUL_FIRE) {
                    setBlockToAirSafe(world, pos);
                } else if ((block == Blocks.LAVA && !blockState.getValue(LiquidBlock.LEVEL).equals(0)) || block == Blocks.MAGMA_BLOCK) {
                    setBlockSafe(world, pos, Blocks.COBBLESTONE.defaultBlockState());
                } else if (block == Blocks.LAVA) {
                    setBlockSafe(world, pos, Blocks.OBSIDIAN.defaultBlockState());
                }
            } else {
                LivingEntity santa = (LivingEntity) lazilySpawnEntity(world, entity, random, "zombie", 1.0f / (150 * 150), pos);
                Entity snowGolem = lazilySpawnEntity(world, entity, random, "snow_golem", 1.0f / (20 * 20), pos);
                if (canSpawnEntity(world, blockState, pos, santa)) {
                    ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
                    helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000, true));
                    ItemStack chestPlate = new ItemStack(Items.LEATHER_CHESTPLATE);
                    chestPlate.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000, true));
                    ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
                    leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000, true));
                    ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
                    boots.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000, true));

                    santa.setItemSlot(EquipmentSlot.HEAD, helmet);
                    santa.setItemSlot(EquipmentSlot.CHEST, chestPlate);
                    santa.setItemSlot(EquipmentSlot.LEGS, leggings);
                    santa.setItemSlot(EquipmentSlot.FEET, boots);
                    santa.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));

                    santa.setCustomName(Component.literal("Hogfather"));
                }
                canSpawnEntity(world, blockState, pos, snowGolem);
            }
            changeBiome(Biomes.SNOWY_TAIGA, pass, effectCenter, serverLevel);
        }
    }
}
