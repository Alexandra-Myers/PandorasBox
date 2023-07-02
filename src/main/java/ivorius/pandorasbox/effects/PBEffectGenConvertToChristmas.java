/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToChristmas extends PBEffectGenerate
{
    public PBEffectGenConvertToChristmas() {}
    public PBEffectGenConvertToChristmas(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerWorld)
        {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0)
            {
                if (isBlockAnyOf(block, Blocks.WATER))
                {
                    setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
                }
                else if (blockState.isAir(world, pos))
                {
                    boolean setSnow = true;

                    BlockPos posBelow = pos.below();
                    if (world.loadedAndEntityCanStandOn(posBelow, entity))
                    {
                        if (random.nextInt(10 * 10) == 0)
                        {
                            setBlockSafe(world, pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(world.random)));
                            ChestTileEntity tileentitychest = (ChestTileEntity) world.getBlockEntity(pos);

                            if (tileentitychest != null)
                            {
                                Collection<RandomizedItemStack> itemSelection = PandorasBoxHelper.blocksAndItems;
                                RandomizedItemStack chestContent = WeightedSelector.selectItem(random, itemSelection);
                                ItemStack stack = chestContent.itemStack.copy();
                                stack.setCount(chestContent.min + random.nextInt(chestContent.max - chestContent.min + 1));

                                tileentitychest.setItem(world.random.nextInt(tileentitychest.getContainerSize()), stack);
                            }

                            setSnow = false;
                        }
                        else if (random.nextInt(10 * 10) == 0)
                        {
                            setBlockSafe(world, pos, Blocks.REDSTONE_LAMP.defaultBlockState());
                            setBlockSafe(world, posBelow, Blocks.REDSTONE_BLOCK.defaultBlockState());
                            setSnow = false;
                        }
                        else if (random.nextInt(10 * 10) == 0)
                        {
                            setBlockSafe(world, pos, Blocks.CAKE.defaultBlockState());
                            setSnow = false;
                        }
                        else if (random.nextInt(10 * 10) == 0)
                        {
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            world.addFreshEntity(entityItem);
                        }
                    }

                    if (setSnow && Blocks.SNOW.defaultBlockState().canSurvive(world, pos))
                    {
                        setBlockSafe(world, pos, Blocks.SNOW.defaultBlockState());
                    }
                }
                else if (block == Blocks.FIRE || block == Blocks.SOUL_FIRE)
                {
                    setBlockToAirSafe(world, pos);
                }
                else if ((block == Blocks.LAVA && !blockState.getValue(FlowingFluidBlock.LEVEL).equals(0)) || block == Blocks.MAGMA_BLOCK)
                {
                    setBlockSafe(world, pos, Blocks.COBBLESTONE.defaultBlockState());
                }
                else if (block == Blocks.LAVA)
                {
                    setBlockSafe(world, pos, Blocks.OBSIDIAN.defaultBlockState());
                }
            }
            else
            {
                Entity santa = lazilySpawnEntity(world, entity, random, "zombie", 1.0f / (150 * 150), pos);
                Entity snowGolem = lazilySpawnEntity(world, entity, random, "snow_golem", 1.0f / (20 * 20), pos);
                if (canSpawnEntity(world, blockState, pos, santa))
                {
                    ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
                    ((DyeableArmorItem) helmet.getItem()).setColor(helmet, 0xff0000);
                    ItemStack chestPlate = new ItemStack(Items.LEATHER_CHESTPLATE);
                    ((DyeableArmorItem) helmet.getItem()).setColor(helmet, 0xff0000);
                    ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
                    ((DyeableArmorItem) helmet.getItem()).setColor(helmet, 0xff0000);
                    ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
                    ((DyeableArmorItem) helmet.getItem()).setColor(helmet, 0xff0000);

                    santa.setItemSlot(EquipmentSlotType.HEAD, helmet);
                    santa.setItemSlot(EquipmentSlotType.CHEST, chestPlate);
                    santa.setItemSlot(EquipmentSlotType.LEGS, leggings);
                    santa.setItemSlot(EquipmentSlotType.FEET, boots);
                    santa.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.STICK));

                    santa.setCustomName(new StringTextComponent("Hogfather"));

                }
                canSpawnEntity(world, blockState, pos, snowGolem);
            }
        }
    }
}
