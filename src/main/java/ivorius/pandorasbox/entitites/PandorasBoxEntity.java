/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.entitites;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.init.DataSerializerInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lukas on 30.03.14.
 */
public class PandorasBoxEntity extends Entity implements OwnableEntity {
    public static final float BOX_UPSCALE_SPEED = 0.02f;
    private static final EntityDataAccessor<Integer> BOX_DEATH_TICKS = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BOX_WAITING_TIME = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EFFECT_TICKS_EXISTED = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.INT);
    protected boolean canGenerateMoreEffectsAfterwards;
    protected boolean floatUp;
    private static final EntityDataAccessor<Float> FLOAT_PROGRESS = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SCALE_PROGRESS = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<PBEffect> DATA_EFFECT_ID = SynchedEntityData.defineId(PandorasBoxEntity.class, DataSerializerInit.PBEFFECTSERIALIZER);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNER_UUID = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<ItemStack> DATA_RENDER_ITEM = SynchedEntityData.defineId(PandorasBoxEntity.class, EntityDataSerializers.ITEM_STACK);

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

    public void setEffectCenter(Vec3 effectCenter) {
        this.effectCenter = effectCenter;
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
        builder.define(DATA_EFFECT_ID, new PBEffectDuplicateBox(PBEffectDuplicateBox.MODE_BOX_IN_BOX));
        builder.define(DATA_OWNER_UUID, Optional.empty());
        builder.define(DATA_RENDER_ITEM, ItemStack.EMPTY);
    }

    public int getTicksForEffect(PBEffect identityEffect) {
        if (getBoxWaitingTime() == 0 && getDeathTicks() < 0) {
            PBEffect effect = getBoxEffect();
            return effect.getTicksExistedForEffect(identityEffect, getEffectTicksExisted());
        }
        return -2;
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
                if (!level.isClientSide())
                    remove(RemovalReason.DISCARDED);
            } else {
                if (effect.isDone(effectTicksExisted)) {
                    if (!level.isClientSide()) {
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
                        setEffectCenter(position());

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
            if (!level.isClientSide()) {
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

    public void setRenderItem(ItemStack renderItem) {
        entityData.set(DATA_RENDER_ITEM, renderItem);
    }

    public ItemStack getRenderItem() {
        return entityData.get(DATA_RENDER_ITEM);
    }

    public void setOwner(@Nullable LivingEntity entity) {
        entityData.set(DATA_OWNER_UUID, Optional.ofNullable(entity).map(EntityReference::of));
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return entityData.get(DATA_OWNER_UUID).orElse(null);
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

    public int getDeathTicks() {
        return getEntityData().get(BOX_DEATH_TICKS);
    }

    public void setDeathTicks(int deathTicks) {
        getEntityData().set(BOX_DEATH_TICKS, deathTicks);
    }

    public float getRatioBoxOpen(float partialTicks) {
        if (getFloatProgress() >= 0.0f)
            return Mth.clamp(((getFloatProgress() + partialTicks * 0.025f - 0.5f) * 2.0f), -0.025f, 1.0f);
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
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        readBoxData(valueInput);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        writeBoxData(valueOutput);
    }

    public void readBoxData(ValueInput valueInput) {
        setBoxEffect(valueInput.read("boxEffect", PBEffect.CODEC.orElseGet(pbEffectError -> {
            PandorasBox.logger.error("Failed to parse box, using fallback. Error: " + pbEffectError);
        }, () -> new PBEffectDuplicateBox(PBEffectDuplicateBox.MODE_BOX_IN_BOX))).get());
        EntityReference<LivingEntity> entityReference = EntityReference.readWithOldOwnerConversion(valueInput, "ownerUUID", this.level());
        if (entityReference != null) {
            this.entityData.set(DATA_OWNER_UUID, Optional.of(entityReference));
        } else {
            this.entityData.set(DATA_OWNER_UUID, Optional.empty());
        }

        setEffectTicksExisted(valueInput.getIntOr("effectTicksExisted", 0));
        setBoxWaitingTime(valueInput.getIntOr("timeBoxWaiting", 0));
        canGenerateMoreEffectsAfterwards = valueInput.getBooleanOr("canGenerateMoreEffectsAfterwards", false);
        setFloatProgress(valueInput.getFloatOr("floatAwayProgress", 0.0F));
        floatUp = valueInput.getBooleanOr("floatUp", false);
        setScale(valueInput.getFloatOr("scaleInProgress", 0.0F));
        Optional<ItemStack> renderItem = valueInput.read("renderItem", ItemStack.CODEC);
        renderItem.ifPresent(this::setRenderItem);

        AtomicBoolean wasError = new AtomicBoolean(false);
        Vec3 effectCenter = valueInput.read("effectCenter", Vec3.CODEC).orElseGet(() -> {
            wasError.set(true);
            return new Vec3(getX(), getY(), getZ());
        });
        if (wasError.get()) {
            Optional<Double> x = valueInput.read("effectCenterX", Codec.DOUBLE);
            Optional<Double> y = valueInput.read("effectCenterY", Codec.DOUBLE);
            Optional<Double> z = valueInput.read("effectCenterZ", Codec.DOUBLE);
            if (x.isPresent() && y.isPresent() && z.isPresent()) effectCenter = new Vec3(x.get(), y.get(), z.get());
        }

        setEffectCenter(effectCenter);
    }

    public void writeBoxData(ValueOutput valueOutput) {
        valueOutput.storeNullable("boxEffect", PBEffect.CODEC, getBoxEffect());
        EntityReference<LivingEntity> entityReference = this.getOwnerReference();
        if (entityReference != null) entityReference.store(valueOutput, "ownerUUID");

        valueOutput.putInt("effectTicksExisted", getEffectTicksExisted());
        valueOutput.putInt("timeBoxWaiting", getBoxWaitingTime());
        valueOutput.putBoolean("canGenerateMoreEffectsAfterwards", canGenerateMoreEffectsAfterwards);
        valueOutput.putFloat("floatAwayProgress", getFloatProgress());
        valueOutput.putBoolean("floatUp", floatUp);
        valueOutput.putFloat("scaleInProgress", getCurrentScale());
        if (!getRenderItem().isEmpty()) valueOutput.store("renderItem", ItemStack.CODEC, getRenderItem());

        valueOutput.store("effectCenter", Vec3.CODEC, effectCenter);
    }
}