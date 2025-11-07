/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.spawn_entities.SpawnEntityIDListEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
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
    public static final ResourceLocation DEFAULT = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "render_default");
    public static final Codec<PBEffect> CODEC = Init.BOX_EFFECT_TYPE_REGISTRY.byNameCodec()
            .dispatch(PBEffect::codec, mapCodec -> mapCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, PBEffect> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public static boolean setBlockToAirSafe(Level level, BlockPos pos) {
        boolean safeDest = level.getBlockState(pos).isAir() || level.getBlockState(pos).getDestroySpeed(level, pos) >= 0f;
        return safeDest && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    public static boolean setBlockSafe(Level level, BlockPos pos, BlockState state) {
        boolean safeDest = level.getBlockState(pos).isAir() || level.getBlockState(pos).getDestroySpeed(level, pos) >= 0f;
        boolean safeSrc = state.isAir() || state.getDestroySpeed(level, pos) >= 0f;

        return safeDest && safeSrc && level.setBlockAndUpdate(pos, state);
    }
    public static boolean setBlockUnsafeSrc(Level level, BlockPos pos, BlockState state) {
        boolean safeDest = level.getBlockState(pos).isAir() || level.getBlockState(pos).getDestroySpeed(level, pos) >= 0f;

        return safeDest && level.setBlockAndUpdate(pos, state);
    }

    public static boolean setBlockVarying(Level level, BlockPos pos, Block block, int unified) {
        return setBlockSafe(level, pos, PandorasBoxHelper.getRandomBlockState(level.random, block, unified));
    }

    public static boolean setBlockVaryingUnsafeSrc(Level level, BlockPos pos, Block block, int unified) {
        return setBlockUnsafeSrc(level, pos, PandorasBoxHelper.getRandomBlockState(level.random, block, unified));
    }

    public static Player getRandomNearbyPlayer(Level level, PandorasBoxEntity box) {
        List<Player> players = level.getEntitiesOfClass(Player.class, box.getBoundingBox().expandTowards(30.0, 30.0, 30.0));
        if (players.isEmpty())
            return null;
        return players.get(box.getRandom().nextInt(players.size()));
    }

    public static Player getPlayer(Level level, PandorasBoxEntity box) {
        EntityReference<LivingEntity> ownerReference = box.getOwnerReference();
        return ownerReference == null || !(box.getOwner() instanceof Player player) ? getRandomNearbyPlayer(level, box) : player;
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

    public static Entity[] lazilyCreateEntities(Level level, PandorasBoxEntity box, RandomSource random, String entityID, float chance, BlockPos pos) {
        if (random.nextFloat() < chance && !level.isClientSide()) {
            return SpawnEntityIDListEffect.createEntity(level, box, random, entityID, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }

        return new Entity[0];
    }
    public static Entity[] lazilySpawnFlyingEntities(Level level, PandorasBoxEntity box, RandomSource random, String entityID, float chance, BlockPos pos) {
        Entity[] entities = lazilyCreateEntities(level, box, random, entityID, chance, pos);
        Entity previousEntity = null;
        for (Entity newEntity : entities) {
            if (newEntity != null) {
                level.addFreshEntity(newEntity);
                if (previousEntity != null) previousEntity.startRiding(newEntity, true, true);
                previousEntity = newEntity;
            }
        }
        return entities;
    }

    public static boolean canSpawnEntities(Level level, BlockPos pos, Entity[] entities) {
        if (level.isClientSide())
            return false;
        boolean success = false;
        Entity previousEntity = null;
        for (Entity newEntity : entities) {
            if (newEntity != null) {
                if (newEntity.isInWall()) {
                    success = false;
                    break;
                }

                if (level.loadedAndEntityCanStandOn(pos.below(), newEntity) && !level.isClientSide()) {
                    level.addFreshEntity(newEntity);
                    if (previousEntity != null) previousEntity.startRiding(newEntity, true, true);
                    previousEntity = newEntity;
                    success = true;
                }
            }
        }

        return success;
    }

    public static boolean canSpawnFlyingEntity(Level level, BlockState block, BlockPos pos) {
        if (level.isClientSide())
            return false;

        return !(block.getLightBlock() > 0 || level.getBlockState(pos.below()).getLightBlock() > 0 || level.getBlockState(pos.below(2)).getLightBlock() > 0);
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

    public abstract int getMaxTicksAlive();

    public abstract @NotNull MapCodec<? extends PBEffect> codec();

    public ResourceLocation rendererResourceLocationForEffect() {
        return DEFAULT;
    }
}
