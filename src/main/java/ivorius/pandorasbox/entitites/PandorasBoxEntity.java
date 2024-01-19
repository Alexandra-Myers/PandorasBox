/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.entitites;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectcreators.PBECDuplicateBox;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.effects.PBEffectRegistry;
import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.random.DConstant;
import ivorius.pandorasbox.random.IConstant;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityAdditionalSpawnData;
import net.neoforged.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by lukas on 30.03.14.
 */
public class PandorasBoxEntity extends Entity implements IEntityAdditionalSpawnData {
    public static final float BOX_UPSCALE_SPEED = 0.02f;

    private static final EntityDataAccessor<Integer> BOX_DEATH_TICKS = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.INT);

    protected int timeBoxWaiting;
    protected int effectTicksExisted;
    protected boolean canGenerateMoreEffectsAfterwards = true;

    protected PBEffect boxEffect;

    protected boolean floatUp = false;
    protected float floatAwayProgress = -1.0f;

    protected float scaleInProgress = 1.0f;
    private static final EntityDataAccessor<PBEffect> DATA_EFFECT_ID = SynchedEntityData.defineId(PandorasBoxEntity.class, Registry.PBEFFECTSERIALIZER.get());
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    protected Vec3 effectCenter = new Vec3(0, 0, 0);

    public PandorasBoxEntity(EntityType<? extends PandorasBoxEntity> p_i50172_1_, Level p_i50172_2_) {
        super(p_i50172_1_, p_i50172_2_);
    }

    @Override
    public boolean canCollideWith(@NotNull Entity p_241849_1_) {
        return false;
    }

    public void setTimeBoxWaiting(int timeBoxWaiting) {
        this.timeBoxWaiting = timeBoxWaiting;
    }

    public int getEffectTicksExisted() {
        return effectTicksExisted;
    }

    public void setCanGenerateMoreEffectsAfterwards(boolean canGenerateMoreEffectsAfterwards) {
        this.canGenerateMoreEffectsAfterwards = canGenerateMoreEffectsAfterwards;
    }

    public Vec3 getEffectCenter() {
        return effectCenter;
    }

    public void setEffectCenter(double x, double y, double z) {
        this.effectCenter = new Vec3(x, y, z);
    }

    public float getCurrentScale() {
        return scaleInProgress;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(BOX_DEATH_TICKS, -1);
        this.getEntityData().define(DATA_EFFECT_ID, new PBECDuplicateBox(new IConstant(PBEffectDuplicateBox.MODE_BOX_IN_BOX), new DConstant(0.5)).constructEffect(this.level(), this.getX(), this.getY(), this.getZ(), this.random));
        this.getEntityData().define(DATA_OWNER_UUID, Optional.empty());
    }

    @Override
    public void tick() {
        Level level = level();
        super.tick();
        if (timeBoxWaiting == 0 && getDeathTicks() < 0) {
            PBEffect effect = getBoxEffect();

            if (effect == null) {
                if (!level.isClientSide)
                    remove(RemovalReason.DISCARDED);
            } else {
                if (effect.isDone(this, effectTicksExisted)) {
                    if (!level.isClientSide) {
                        boolean isCompletelyDone = true;

                        if (canGenerateMoreEffectsAfterwards && effect.canGenerateMoreEffectsAfterwards(this))
                            if (random.nextFloat() < PandorasBox.CONFIG.boxLongevity.get()) {
                                startNewEffect();

                                isCompletelyDone = false;
                            }

                        if (isCompletelyDone)
                            startFadingOut();
                    }
                } else {
                    if (effectTicksExisted == 0)
                        setEffectCenter(getX(), getY(), getZ());

                    effect.doTick(this, effectCenter, effectTicksExisted);
                }
            }
        }

        if (timeBoxWaiting == 0) {
            setDeltaMovement(getDeltaMovement().scale(0.5));
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.95));
        }

        if (floatAwayProgress >= 0.0f && floatAwayProgress < 1.0f) {
            float speed = Mth.square(floatAwayProgress - 0.7f);
            if (floatUp) {
                setDeltaMovement(getDeltaMovement().add(0, speed * 0.015f, 0));
            } else {
                moveRelative(0.4f, new Vec3(-0.00f, speed * 0.02f, -0.02f));
                setDeltaMovement(getDeltaMovement().add(0, speed * 0.015f, 0));
            }

            floatAwayProgress += 0.025f;

            if (floatAwayProgress > 1.0f)
                stopFloating();
        }

        if (scaleInProgress < 1.0f)
            scaleInProgress += BOX_UPSCALE_SPEED;
        if (scaleInProgress > 1.0f)
            scaleInProgress = 1.0f;

        this.move(MoverType.SELF, getDeltaMovement());

        if (timeBoxWaiting == 0) {
            if (getDeathTicks() < 0) {
                if (!isInvisible() && level.isClientSide()) {
                    double yCenter = getY() + this.getBbHeight() * 0.5;

                    for (int e = 0; e < 2; e++) {
                        double xP = (random.nextDouble() - random.nextDouble()) * 0.2;
                        double yDir = random.nextDouble() * 0.1;
                        double zP = (random.nextDouble() - random.nextDouble()) * 0.2;

                        level.addParticle(ParticleTypes.SMOKE, getX() + xP, yCenter, getZ() + zP, 0.0D, yDir, 0.0D);
                    }
                    for (int e = 0; e < 3; e++) {
                        double xDir = (random.nextDouble() - random.nextDouble()) * 3.0;
                        double yDir = random.nextDouble() * 4.0 + 2.0;
                        double zDir = (random.nextDouble() - random.nextDouble()) * 3.0;

                        double xP = (random.nextDouble() - 0.5) * getBbWidth();
                        double zP = (random.nextDouble() - 0.5) * getBbWidth();

                        level.addParticle(ParticleTypes.ENCHANT, getX() + xP + xDir, yCenter + yDir, getZ() + zP + zDir, -xDir, -yDir, -zDir);
                    }
                    for (int e = 0; e < 4; e++) {
                        double xP = (random.nextDouble() * 16) - 8D;
                        double yP = (random.nextDouble() * 5) - 2D;
                        double zP = (random.nextDouble() * 16D) - 8D;

                        double xDir = (random.nextDouble() * 2D) - 1D;
                        double yDir = (random.nextDouble() * 2D) - 1D;
                        double zDir = (random.nextDouble() * 2D) - 1D;

                        level.addParticle(ParticleTypes.PORTAL, getX() + xP, yCenter + yP, getZ() + zP, xDir, yDir, zDir);
                    }
                }

                effectTicksExisted++;
            }
        } else
            timeBoxWaiting--;

        if (getDeathTicks() >= 0) {
            if (!level.isClientSide) {
                if (getDeathTicks() >= 30)
                    remove(RemovalReason.DISCARDED);
            } else {
                for (int e = 0; e < Math.min(getDeathTicks(), 60); e++) {
                    double xP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double yP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double zP = (random.nextDouble() - random.nextDouble()) * 0.5;

                    level().addParticle(ParticleTypes.SMOKE, getX() + xP, getY() + yP, getZ() + zP, 0.0D, 0.0D, 0.0D);
                }
            }

            setDeathTicks(getDeathTicks() + 1);
        }
    }
    public void startNewEffect() {
        effectTicksExisted = 0;
        timeBoxWaiting = random.nextInt(40);

        boxEffect = ensureNotNull(PBECRegistry.createRandomEffect(level(), random, effectCenter.x, effectCenter.y, effectCenter.z, true));
        entityData.set(DATA_EFFECT_ID, boxEffect);
    }

    public void setBoxOwnerUUID(UUID uuid) {
        if (uuid == null) return;
        entityData.set(DATA_OWNER_UUID, Optional.of(uuid));
    }

    public UUID getBoxOwnerUUID() {
        if (entityData.get(DATA_OWNER_UUID).isEmpty())
            return null;
        return entityData.get(DATA_OWNER_UUID).get();
    }

    public void setBoxOwner(Player player) {
        if (player == null) return;
        entityData.set(DATA_OWNER_UUID, Optional.of(player.getUUID()));
    }

    public Player getBoxOwner() {
        if (entityData.get(DATA_OWNER_UUID).isEmpty())
            return null;
        return level().getPlayerByUUID(entityData.get(DATA_OWNER_UUID).get());
    }

    public void startFadingOut() {
        setDeathTicks(0);
    }

    public void beginFloatingAway() {
        floatAwayProgress = 0.0f;
        floatUp = false;
    }

    public void beginFloatingUp() {
        floatAwayProgress = 0.0f;
        floatUp = true;
    }

    public void stopFloating() {
        floatAwayProgress = -1.0f;
        effectTicksExisted = 0;
    }

    public void beginScalingIn() {
        scaleInProgress = 0.0f;
    }

    public PBEffect getBoxEffect() {
        return entityData.get(DATA_EFFECT_ID);
    }

    public void setBoxEffect(PBEffect effect) {
        boxEffect = ensureNotNull(effect);
        entityData.set(DATA_EFFECT_ID, boxEffect);
    }
    public PBEffect ensureNotNull(PBEffect input) {
        while(input == null) {
            input = PBECRegistry.createRandomEffect(level(), random, effectCenter.x, effectCenter.y, effectCenter.z, true);
        }
        return input;
    }

    public RandomSource getRandom() {
        return random;
    }

    public int getDeathTicks() {
        return getEntityData().get(BOX_DEATH_TICKS);
    }

    public void setDeathTicks(int deathTicks) {
        getEntityData().set(BOX_DEATH_TICKS, deathTicks);
    }

    public float getRatioBoxOpen(float partialTicks) {
        if (floatAwayProgress >= 0.0f)
            return Mth.clamp(((floatAwayProgress + partialTicks * 0.025f - 0.5f) * 2.0f), 0.0f, 1.0f);
        else
            return 1.0f;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void push(@NotNull Entity entityIn) {
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        readBoxData(compound);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        writeBoxData(compound);
    }

    public void readBoxData(CompoundTag compound) {
        setBoxEffect(PBEffectRegistry.loadEffect(compound.getCompound("boxEffect")));
        if (compound.contains("ownerUUID"))
            setBoxOwnerUUID(compound.getUUID("ownerUUID"));

        effectTicksExisted = compound.getInt("effectTicksExisted");
        timeBoxWaiting = compound.getInt("timeBoxWaiting");
        canGenerateMoreEffectsAfterwards = compound.getBoolean("canGenerateMoreEffectsAfterwards");
        floatAwayProgress = compound.getFloat("floatAwayProgress");
        floatUp = compound.getBoolean("floatUp");
        scaleInProgress = compound.getFloat("scaleInProgress");

        if (compound.contains("effectCenterX", 6) && compound.contains("effectCenterY", 6) && compound.contains("effectCenterZ", 6))
            setEffectCenter(compound.getDouble("effectCenterX"), compound.getDouble("effectCenterY"), compound.getDouble("effectCenterZ"));
        else
            setEffectCenter(getX(), getY(), getZ());
    }

    public void writeBoxData(CompoundTag compound) {
        CompoundTag effectCompound = new CompoundTag();
        PBEffectRegistry.writeEffect(getBoxEffect(), effectCompound);
        compound.put("boxEffect", effectCompound);
        UUID uuid = getBoxOwnerUUID();
        if (uuid != null)
            compound.putUUID("ownerUUID", uuid);

        compound.putInt("effectTicksExisted", effectTicksExisted);
        compound.putInt("timeBoxWaiting", timeBoxWaiting);
        compound.putBoolean("canGenerateMoreEffectsAfterwards", canGenerateMoreEffectsAfterwards);
        compound.putFloat("floatAwayProgress", floatAwayProgress);
        compound.putBoolean("floatUp", floatUp);
        compound.putFloat("scaleInProgress", scaleInProgress);

        compound.putDouble("effectCenterX", effectCenter.x);
        compound.putDouble("effectCenterY", effectCenter.y);
        compound.putDouble("effectCenterZ", effectCenter.z);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag compound = new CompoundTag();
        writeBoxData(compound);
        buffer.writeNbt(compound);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        CompoundTag compound = additionalData.readNbt();

        if (compound != null)
            readBoxData(compound);
    }
}
