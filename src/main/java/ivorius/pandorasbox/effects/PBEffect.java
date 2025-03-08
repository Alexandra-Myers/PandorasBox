/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.spawn_entities.SpawnEntityIDListEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffect {
    public static final Codec<PBEffect> CODEC = Init.BOX_EFFECT_TYPE_REGISTRY.byNameCodec()
            .dispatch(PBEffect::codec, mapCodec -> mapCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, PBEffect> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public static boolean setBlockToAirSafe(Level world, BlockPos pos) {
        boolean safeDest = world.getBlockState(pos).isAir() || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;
        return safeDest && world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    public static boolean setBlockSafe(Level world, BlockPos pos, BlockState state) {
        boolean safeDest = world.getBlockState(pos).isAir() || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;
        boolean safeSrc = state.isAir()|| state.getDestroySpeed(world, pos) >= 0f;

        return safeDest && safeSrc && world.setBlockAndUpdate(pos, state);
    }
    public static boolean setBlockUnsafeSrc(Level world, BlockPos pos, BlockState state) {
        boolean safeDest = world.getBlockState(pos).isAir() || world.getBlockState(pos).getDestroySpeed(world, pos) >= 0f;

        return safeDest && world.setBlockAndUpdate(pos, state);
    }

    public static boolean setBlockVarying(Level world, BlockPos pos, Block block, int unified) {
        return setBlockSafe(world, pos, PandorasBoxHelper.getRandomBlockState(world.random, block, unified));
    }

    public static boolean setBlockVaryingUnsafeSrc(Level world, BlockPos pos, Block block, int unified) {
        return setBlockUnsafeSrc(world, pos, PandorasBoxHelper.getRandomBlockState(world.random, block, unified));
    }

    public static Player getRandomNearbyPlayer(Level world, PandorasBoxEntity box) {
        List<Player> players = world.getEntitiesOfClass(Player.class, box.getBoundingBox().expandTowards(30.0, 30.0, 30.0));
        if (players.isEmpty())
            return null;
        return players.get(box.getRandom().nextInt(players.size()));
    }

    public static Player getPlayer(Level world, PandorasBoxEntity box) {
        Player player = box.getBoxOwner();
        return player == null ? getRandomNearbyPlayer(world, box) : player;
    }

    @SafeVarargs
    public static boolean isBlockAnyOf(@NotNull Block block, Either<Block, TagKey<Block>>... blocks) {
        for (Either<Block, TagKey<Block>> match : blocks) {
            Block other = match.left().orElse(null);
            TagKey<Block> tag = match.right().orElse(null);
            if (block == other) return true;
            else if (tag != null && block.defaultBlockState().is(tag)) return true;
        }

        return false;
    }

    public static Entity lazilySpawnEntity(Level world, PandorasBoxEntity box, RandomSource random, String entityID, float chance, BlockPos pos) {
        if (random.nextFloat() < chance && !world.isClientSide()) {
            return SpawnEntityIDListEffect.createEntity(world, box, random, entityID, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }

        return null;
    }
    public static Entity lazilySpawnFlyingEntity(Level world, PandorasBoxEntity box, RandomSource random, String entityID, float chance, BlockPos pos) {
        Entity entity =  lazilySpawnEntity(world, box, random, entityID, chance, pos);
        if(entity != null)
            world.addFreshEntity(entity);
        return entity;
    }

    public static boolean canSpawnEntity(Level world, BlockState block, BlockPos pos, Entity entity) {
        if(entity == null) return false;
        if (world.isClientSide())
            return false;

        if (block.getLightBlock() > 0)
            return false;
        if(world.loadedAndEntityCanStandOn(pos.below(), entity) && !world.isClientSide()) {
            world.addFreshEntity(entity);
            return true;
        }

        return false;
    }

    public static boolean canSpawnFlyingEntity(Level world, BlockState block, BlockPos pos) {
        if (world.isClientSide())
            return false;

        return !(block.getLightBlock() > 0 || world.getBlockState(pos.below()).getLightBlock() > 0 || world.getBlockState(pos.below(2)).getLightBlock() > 0);
    }

    public static void combinedEffectDuration(LivingEntity entity, MobEffectInstance[] mobEffects) {
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

    public abstract boolean isDone(int ticksAlive);

    public abstract boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity);

    public abstract int getTicksExistedForEffect(PBEffect identityEffect, int ticksAlive);

    public abstract @NotNull MapCodec<? extends PBEffect> codec();
}
