/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffect
{
    public String getEffectID()
    {
        return PBEffectRegistry.getEffectID(this);
    }

    public static boolean setBlockToAirSafe(World world, BlockPos pos)
    {
        boolean safeDest = world.getBlockState(pos).isAir(world, pos) || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;
        return safeDest && world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    public static boolean setBlockSafe(World world, BlockPos pos, BlockState state)
    {
        boolean safeDest = world.getBlockState(pos).isAir(world, pos) || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;
        boolean safeSrc = state.isAir(world, pos)|| state.getDestroySpeed(world, pos) >= 0f;

        return safeDest && safeSrc && world.setBlockAndUpdate(pos, state);
    }

    public static boolean setBlockVarying(World world, BlockPos pos, Block block, int unified)
    {
        return setBlockSafe(world, pos, PandorasBoxHelper.getRandomBlockState(world.random, block, unified));
    }

    public static PlayerEntity getRandomNearbyPlayer(World world, EntityPandorasBox box)
    {
        return (PlayerEntity) world.getEntitiesOfClass(PlayerEntity.class, box.getBoundingBox().expandTowards(30.0, 30.0, 30.0));
    }

    public static PlayerEntity getPlayer(World world, EntityPandorasBox box)
    {
        return getRandomNearbyPlayer(world, box); // We don't know the owner :/
    }

    public static boolean isBlockAnyOf(Block block, Block... blocks)
    {
        for (Block block1 : blocks)
        {
            if (block == block1)
                return true;
        }

        return false;
    }
    public static boolean isBlockAnyOf(Block block, List<Block> blocks)
    {
        for (Block block1 : blocks)
        {
            if (block == block1)
                return true;
        }

        return false;
    }

    public static Entity lazilySpawnEntity(World world, EntityPandorasBox box, Random random, String entityID, float chance, BlockPos pos)
    {
        if (random.nextFloat() < chance && !world.isClientSide())
        {
            Entity entity = PBEffectSpawnEntityIDList.createEntity(world, box, random, entityID, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            if (entity != null)
            {
                return entity;
            }
        }

        return null;
    }
    public static Entity lazilySpawnFlyingEntity(World world, EntityPandorasBox box, Random random, String entityID, float chance, BlockPos pos) {
        Entity entity =  lazilySpawnEntity(world, box, random, entityID, chance, pos);
        if(entity != null) world.addFreshEntity(entity);
        return entity;
    }

    public static boolean canSpawnEntity(World world, BlockState block, BlockPos pos, Entity entity)
    {
        if(entity == null) return false;
        if (world instanceof ClientWorld)
            return false;

        if (block.getLightBlock(world, pos) > 0)
            return false;
        if(world.loadedAndEntityCanStandOn(pos.below(), entity) && !world.isClientSide()) {
            world.addFreshEntity(entity);
            return true;
        }

        return false;
    }

    public boolean canSpawnFlyingEntity(World world, BlockState block, BlockPos pos)
    {
        if (world instanceof ClientWorld)
            return false;

        return !(block.getLightBlock(world, pos) > 0 || world.getBlockState(pos.below()).getLightBlock(world, pos.below()) > 0 || world.getBlockState(pos.below(2)).getLightBlock(world, pos.below(2)) > 0);
    }

    public void addPotionEffectDuration(LivingEntity entity, Potion potionEffect)
    {
        for(EffectInstance effectInstance : potionEffect.getEffects()) {
            if (entity.canBeAffected(effectInstance)) {
                boolean addNewEffect = true;

                if (entity.hasEffect(effectInstance.getEffect())) {
                    EffectInstance prevEffect = entity.getEffect(effectInstance.getEffect());
                    if (prevEffect.getAmplifier() == effectInstance.getAmplifier()) {
                        int duration = prevEffect.getDuration() + effectInstance.getDuration();
                        EffectInstance combined = new EffectInstance(effectInstance.getEffect(), duration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible());
                        entity.addEffect(combined);
                    }
                }

                entity.addEffect(effectInstance);
            }
        }
    }

    public abstract void doTick(EntityPandorasBox entity, Vec3d effectCenter, int ticksAlive);

    public abstract boolean isDone(EntityPandorasBox entity, int ticksAlive);

    public abstract void writeToNBT(CompoundNBT compound);

    public abstract void readFromNBT(CompoundNBT compound);

    public abstract boolean canGenerateMoreEffectsAfterwards(EntityPandorasBox entity);
}
