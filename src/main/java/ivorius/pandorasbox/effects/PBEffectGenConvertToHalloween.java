/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.google.common.collect.Lists;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;

import static ivorius.pandorasbox.effects.PBEffectGenConvertToNether.makeResolver;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHalloween extends PBEffectGenerate
{
    public PBEffectGenConvertToHalloween() {}
    public PBEffectGenConvertToHalloween(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerLevel serverLevel)
        {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0)
            {
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);

                if (Block.isShapeFullBlock(blockBelowState.getBlockSupportShape(world, posBelow)) && blockState.isAir() && block != Blocks.WATER)
                {
                    if (random.nextInt(5 * 5) == 0)
                    {
                        int b = world.random.nextInt(7);

                        if (b == 0)
                        {
                            setBlockSafe(world, posBelow, Blocks.NETHERRACK.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                        }
                        else if (b == 1)
                        {
                            setBlockSafe(world, pos, Blocks.JACK_O_LANTERN.defaultBlockState());
                        }
                        else if (b == 2)
                        {
                            setBlockSafe(world, pos, Blocks.PUMPKIN.defaultBlockState());
                        }
                        else if (b == 3)
                        {
                            setBlockSafe(world, posBelow, Blocks.FARMLAND.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.PUMPKIN_STEM.getStateDefinition().getPossibleStates().get(world.random.nextInt(4) + 4));
                        }
                        else if (b == 4)
                        {
                            setBlockSafe(world, pos, Blocks.CAKE.defaultBlockState());
                        }
                        else if(b == 5) {
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            world.addFreshEntity(entityItem);
                        } else {
                            setBlockSafe(world, posBelow, Blocks.SOUL_SOIL.defaultBlockState());
                            setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                        }
                    }
                }
            }
            else
            {
                ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilySpawnEntity(world, entity, random, "zombified_piglin", 1.0f / (20 * 20), pos),
                        lazilySpawnEntity(world, entity, random, "enderman", 1.0f / (20 * 20), pos),
                        lazilySpawnEntity(world, entity, random, "phantom", 1.0f / (20 * 20), pos));
                for (Entity entity1 : entities) {
                    canSpawnEntity(world, blockState, pos, entity1);
                }
            }
            changeBiome(Biomes.SOUL_SAND_VALLEY, pass, effectCenter, serverLevel);
        }
    }
}
