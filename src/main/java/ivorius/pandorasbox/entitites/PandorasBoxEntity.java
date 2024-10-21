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
import ivorius.pandorasbox.init.DataSerializerInit;
import ivorius.pandorasbox.random.DConstant;
import ivorius.pandorasbox.random.IConstant;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by lukas on 30.03.14.
 */
public class PandorasBoxEntity extends Entity implements PowerableMob {
    public static final float BOX_UPSCALE_SPEED = 0.02f;
    private static final EntityDataAccessor<Integer> BOX_DEATH_TICKS = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BOX_WAITING_TIME = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EFFECT_TICKS_EXISTED = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.INT);
    protected boolean canGenerateMoreEffectsAfterwards;
    protected boolean floatUp;
    private static final EntityDataAccessor<Float> FLOAT_PROGRESS = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SCALE_PROGRESS = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<PBEffect> DATA_EFFECT_ID = SynchedEntityData.defineId(PandorasBoxEntity.class, DataSerializerInit.PBEFFECTSERIALIZER);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    protected Vec3 effectCenter = new Vec3(0, 0, 0);

    public PandorasBoxEntity(EntityType<? extends PandorasBoxEntity> p_i50172_1_, Level p_i50172_2_) {
        super(p_i50172_1_, p_i50172_2_);
        canGenerateMoreEffectsAfterwards = true;
        floatUp = false;
    }

    public PandorasBoxEntity(EntityType<? extends PandorasBoxEntity> entityType, Level level, boolean canGenerateMoreEffectsAfterwards, boolean floatUp) {
        super(entityType, level);
        this.canGenerateMoreEffectsAfterwards = canGenerateMoreEffectsAfterwards;
        this.floatUp = floatUp;
    }

    @Override
    public boolean canCollideWith(@NotNull Entity p_241849_1_) {
        return false;
    }

    public Vec3 getEffectCenter() {
        return effectCenter;
    }

    public void setEffectCenter(double x, double y, double z) {
        this.effectCenter = new Vec3(x, y, z);
    }

    public int getBoxWaitingTime() {
        return entityData.get(BOX_WAITING_TIME);
    }

    public void setBoxWaitingTime(int amount) {
        entityData.set(BOX_WAITING_TIME, amount);
    }

    public int getEffectTicksExisted() {
        return entityData.get(EFFECT_TICKS_EXISTED);
    }

    public void setEffectTicksExisted(int amount) {
        entityData.set(EFFECT_TICKS_EXISTED, amount);
    }

    public float getCurrentScale() {
        return entityData.get(SCALE_PROGRESS);
    }

    public void setScale(float amount) {
        entityData.set(SCALE_PROGRESS, amount);
    }

    public float getFloatProgress() {
        return entityData.get(FLOAT_PROGRESS);
    }

    public void setFloatProgress(float amount) {
        entityData.set(FLOAT_PROGRESS, amount);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BOX_DEATH_TICKS, -1);
        builder.define(BOX_WAITING_TIME, -1);
        builder.define(EFFECT_TICKS_EXISTED, -1);
        builder.define(FLOAT_PROGRESS, -1F);
        builder.define(SCALE_PROGRESS, 1F);
        builder.define(DATA_EFFECT_ID, new PBECDuplicateBox(new IConstant(PBEffectDuplicateBox.MODE_BOX_IN_BOX), new DConstant(0.5)).constructEffect(this.level(), this.getX(), this.getY(), this.getZ(), this.random));
        builder.define(DATA_OWNER_UUID, Optional.empty());
    }

    @Override
    public void tick() {
        Level level = level();
        super.tick();
        int timeBoxWaiting = getBoxWaitingTime();
        int effectTicksExisted = getEffectTicksExisted();
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

        float floatAwayProgress = getFloatProgress();
        if (floatAwayProgress >= 0.0f && floatAwayProgress < 1.0f) {
            float speed = Mth.square(floatAwayProgress - 0.7f);
            if (floatUp) {
                setDeltaMovement(getDeltaMovement().add(0, speed * 0.015f, 0));
            } else {
                moveRelative(0.4f, new Vec3(-0.00f, speed * 0.02f, -0.02f));
                setDeltaMovement(getDeltaMovement().add(0, speed * 0.015f, 0));
            }

            floatAwayProgress += 0.025f;
            setFloatProgress(floatAwayProgress);

            if (floatAwayProgress > 1.0f)
                stopFloating();
        }

        float scaleInProgress = getCurrentScale();
        if (scaleInProgress < 1.0f)
            scaleInProgress += BOX_UPSCALE_SPEED;
        if (scaleInProgress > 1.0f)
            scaleInProgress = 1.0f;
        setScale(scaleInProgress);

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
                setEffectTicksExisted(effectTicksExisted);
            }
        } else {
            timeBoxWaiting--;
            setBoxWaitingTime(timeBoxWaiting);
        }

        int deathTicks = getDeathTicks();
        if (deathTicks >= 0) {
            if (!level.isClientSide) {
                if (deathTicks >= 30)
                    remove(RemovalReason.DISCARDED);
            } else {
                for (int e = 0; e < Math.min(deathTicks, 60); e++) {
                    double xP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double yP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double zP = (random.nextDouble() - random.nextDouble()) * 0.5;

                    level().addParticle(ParticleTypes.SMOKE, getX() + xP, getY() + yP, getZ() + zP, 0.0D, 0.0D, 0.0D);
                }
            }

            setDeathTicks(deathTicks + 1);
        }
    }

    public void startNewEffect() {
        setEffectTicksExisted(0);
        setBoxWaitingTime(random.nextInt(40));

        entityData.set(DATA_EFFECT_ID, ensureNotNull(PBECRegistry.createRandomEffect(level(), random, effectCenter.x, effectCenter.y, effectCenter.z, true)));
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

    public void beginFloating() {
        setFloatProgress(0);
    }

    public void stopFloating() {
        setFloatProgress(-1);
        setEffectTicksExisted(0);
    }

    public void beginScalingIn() {
        setScale(0);
    }

    public PBEffect getBoxEffect() {
        return entityData.get(DATA_EFFECT_ID);
    }

    public void setBoxEffect(PBEffect effect) {
        entityData.set(DATA_EFFECT_ID, ensureNotNull(effect), true);
    }

    public PBEffect ensureNotNull(PBEffect input) {
        while (input == null) {
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
        if (getFloatProgress() >= 0.0f)
            return Mth.clamp(((getFloatProgress() + partialTicks * 0.025f - 0.5f) * 2.0f), 0.0f, 1.0f);
        else
            return 1.0f;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        int data = canGenerateMoreEffectsAfterwards ? 1 : 0;
        data <<= 1;
        data |= floatUp ? 1 : 0;
        return new ClientboundAddEntityPacket(this, serverEntity, data);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        canGenerateMoreEffectsAfterwards = (clientboundAddEntityPacket.getData() >> 1) == 1;
        floatUp = ((clientboundAddEntityPacket.getData() << 31) >>> 31) == 1;
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
        setBoxEffect(PBEffectRegistry.loadEffect(compound.getCompound("boxEffect"), registryAccess()));
        if (compound.contains("ownerUUID"))
            setBoxOwnerUUID(compound.getUUID("ownerUUID"));

        setEffectTicksExisted(compound.getInt("effectTicksExisted"));
        setBoxWaitingTime(compound.getInt("timeBoxWaiting"));
        canGenerateMoreEffectsAfterwards = compound.getBoolean("canGenerateMoreEffectsAfterwards");
        setFloatProgress(compound.getFloat("floatAwayProgress"));
        floatUp = compound.getBoolean("floatUp");
        setScale(compound.getFloat("scaleInProgress"));

        if (compound.contains("effectCenterX", 6) && compound.contains("effectCenterY", 6) && compound.contains("effectCenterZ", 6))
            setEffectCenter(compound.getDouble("effectCenterX"), compound.getDouble("effectCenterY"), compound.getDouble("effectCenterZ"));
        else
            setEffectCenter(getX(), getY(), getZ());
    }

    public void writeBoxData(CompoundTag compound) {
        CompoundTag effectCompound = new CompoundTag();
        PBEffectRegistry.writeEffect(getBoxEffect(), effectCompound, registryAccess());
        compound.put("boxEffect", effectCompound);
        UUID uuid = getBoxOwnerUUID();
        if (uuid != null)
            compound.putUUID("ownerUUID", uuid);

        compound.putInt("effectTicksExisted", getEffectTicksExisted());
        compound.putInt("timeBoxWaiting", getBoxWaitingTime());
        compound.putBoolean("canGenerateMoreEffectsAfterwards", canGenerateMoreEffectsAfterwards);
        compound.putFloat("floatAwayProgress", getFloatProgress());
        compound.putBoolean("floatUp", floatUp);
        compound.putFloat("scaleInProgress", getCurrentScale());

        compound.putDouble("effectCenterX", effectCenter.x);
        compound.putDouble("effectCenterY", effectCenter.y);
        compound.putDouble("effectCenterZ", effectCenter.z);
    }

    @Override
    public boolean isPowered() {
        return true;
    }
}