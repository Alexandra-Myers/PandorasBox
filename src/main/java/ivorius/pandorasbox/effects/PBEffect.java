/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffect
{
    public String getEffectID()
    {
        return PBEffectRegistry.getEffectID(this);
    }

    public static boolean setBlockToAirSafe(Level world, BlockPos pos)
    {
        boolean safeDest = world.getBlockState(pos).isAir() || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;
        return safeDest && world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    public static boolean setBlockSafe(Level world, BlockPos pos, BlockState state)
    {
        boolean safeDest = world.getBlockState(pos).isAir() || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;
        boolean safeSrc = state.isAir()|| state.getDestroySpeed(world, pos) >= 0f;

        return safeDest && safeSrc && world.setBlockAndUpdate(pos, state);
    }
    public static boolean setBlockUnsafeSrc(Level world, BlockPos pos, BlockState state)
    {
        boolean safeDest = world.getBlockState(pos).isAir() || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;

        return safeDest && world.setBlockAndUpdate(pos, state);
    }

    public static boolean setBlockVarying(Level world, BlockPos pos, Block block, int unified)
    {
        return setBlockSafe(world, pos, PandorasBoxHelper.getRandomBlockState(world.random, block, unified));
    }

    public static boolean setBlockVaryingUnsafeSrc(Level world, BlockPos pos, Block block, int unified)
    {
        return setBlockUnsafeSrc(world, pos, PandorasBoxHelper.getRandomBlockState(world.random, block, unified));
    }

    public static Player getRandomNearbyPlayer(Level world, PandorasBoxEntity box)
    {
        List<Player> players = world.getEntitiesOfClass(Player.class, box.getBoundingBox().expandTowards(30.0, 30.0, 30.0));
        return players.get(box.getRandom().nextInt(players.size()));
    }

    public static Player getPlayer(Level world, PandorasBoxEntity box)
    {
        Player player = box.getBoxOwner();
        return player == null ? getRandomNearbyPlayer(world, box) : player;
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

    public static Entity lazilySpawnEntity(Level world, PandorasBoxEntity box, RandomSource random, String entityID, float chance, BlockPos pos) {
        if (random.nextFloat() < chance && !world.isClientSide()) {
            return PBEffectSpawnEntityIDList.createEntity(world, box, random, entityID, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }

        return null;
    }
    public static Entity lazilySpawnFlyingEntity(Level world, PandorasBoxEntity box, RandomSource random, String entityID, float chance, BlockPos pos) {
        Entity entity =  lazilySpawnEntity(world, box, random, entityID, chance, pos);
        if(entity != null)
            world.addFreshEntity(entity);
        return entity;
    }

    public static boolean canSpawnEntity(Level world, BlockState block, BlockPos pos, Entity entity)
    {
        if(entity == null) return false;
        if (world.isClientSide())
            return false;

        if (block.getLightBlock(world, pos) > 0)
            return false;
        if(world.loadedAndEntityCanStandOn(pos.below(), entity) && !world.isClientSide()) {
            world.addFreshEntity(entity);
            return true;
        }

        return false;
    }

    public boolean canSpawnFlyingEntity(Level world, BlockState block, BlockPos pos)
    {
        if (world.isClientSide())
            return false;

        return !(block.getLightBlock(world, pos) > 0 || world.getBlockState(pos.below()).getLightBlock(world, pos.below()) > 0 || world.getBlockState(pos.below(2)).getLightBlock(world, pos.below(2)) > 0);
    }

    public void combinedEffectDuration(LivingEntity entity, MobEffectInstance[] mobEffects)
    {
        for(MobEffectInstance effectInstance : mobEffects) {
            if(effectInstance == null)
                continue;
            if (entity.canBeAffected(effectInstance)) {
                if (entity.hasEffect(effectInstance.getEffect())) {
                    MobEffectInstance prevEffect = entity.getEffect(effectInstance.getEffect());
                    if (prevEffect != null && prevEffect.getAmplifier() == effectInstance.getAmplifier()) {
                        int duration = prevEffect.getDuration() + effectInstance.getDuration();
                        MobEffectInstance combined = new MobEffectInstance(effectInstance.getEffect(), duration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible());
                        entity.addEffect(combined);
                    }
                }

                entity.addEffect(effectInstance);
            }
        }
    }

    public abstract void doTick(PandorasBoxEntity entity, Vec3 effectCenter, int ticksAlive);

    public abstract boolean isDone(PandorasBoxEntity entity, int ticksAlive);

    public abstract void writeToNBT(CompoundTag compound);

    public abstract void readFromNBT(CompoundTag compound);

    public abstract boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity);
}
