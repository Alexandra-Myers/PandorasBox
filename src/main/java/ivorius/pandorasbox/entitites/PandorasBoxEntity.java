/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.entitites;

import ivorius.pandorasbox.PBConfig;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectRegistry;
import ivorius.pandorasbox.effects.Vec3d;
import ivorius.pandorasbox.network.PartialUpdateHandler;
import net.minecraft.entity.*;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PandorasBoxEntity extends Entity implements IEntityAdditionalSpawnData, PartialUpdateHandler
{
    public static final float BOX_UPSCALE_SPEED = 0.02f;

    private static final DataParameter<Integer> BOX_DEATH_TICKS = EntityDataManager.defineId(PandorasBoxEntity.class, DataSerializers.INT);

    protected int timeBoxWaiting;
    protected int effectTicksExisted;
    protected boolean canGenerateMoreEffectsAfterwards = true;

    protected PBEffect boxEffect;

    protected boolean floatUp = false;
    protected float floatAwayProgress = -1.0f;
    public static final IDataSerializer<PBEffect> PBEFFECT_SERIALIZER = new IDataSerializer<PBEffect>() {
        public void write(PacketBuffer p_187160_1_, PBEffect p_187160_2_) {
            CompoundNBT compound = new CompoundNBT();
            CompoundNBT effectCompound = new CompoundNBT();
            PBEffectRegistry.writeEffect(p_187160_2_, effectCompound);
            compound.put("boxEffect", effectCompound);
            p_187160_1_.writeNbt(compound);
        }

        public PBEffect read(PacketBuffer p_187159_1_) {
            return PBEffectRegistry.loadEffect(p_187159_1_.readNbt().getCompound("boxEffect"));
        }

        public PBEffect copy(PBEffect p_192717_1_) {
            return p_192717_1_;
        }
    };

    protected float scaleInProgress = 1.0f;
    private static final DataParameter<PBEffect> DATA_EFFECT_ID = EntityDataManager.defineId(PandorasBoxEntity.class, PBEFFECT_SERIALIZER);

    protected Vec3d effectCenter = new Vec3d(0, 0, 0);

    public PandorasBoxEntity(EntityType<? extends PandorasBoxEntity> p_i50172_1_, World p_i50172_2_) {
        super(p_i50172_1_, p_i50172_2_);
    }

    public int getTimeBoxWaiting()
    {
        return timeBoxWaiting;
    }

    @Override
    public boolean canCollideWith(Entity p_241849_1_) {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void setTimeBoxWaiting(int timeBoxWaiting)
    {
        this.timeBoxWaiting = timeBoxWaiting;
    }

    public int getEffectTicksExisted()
    {
        return effectTicksExisted;
    }

    public void setEffectTicksExisted(int effectTicksExisted)
    {
        this.effectTicksExisted = effectTicksExisted;
    }

    public boolean canGenerateMoreEffectsAfterwards()
    {
        return canGenerateMoreEffectsAfterwards;
    }

    public void setCanGenerateMoreEffectsAfterwards(boolean canGenerateMoreEffectsAfterwards)
    {
        this.canGenerateMoreEffectsAfterwards = canGenerateMoreEffectsAfterwards;
    }

    public boolean floatUp()
    {
        return floatUp;
    }

    public void setFloatUp(boolean floatUp)
    {
        this.floatUp = floatUp;
    }

    public float getFloatAwayProgress()
    {
        return floatAwayProgress;
    }

    public void setFloatAwayProgress(float floatAwayProgress)
    {
        this.floatAwayProgress = floatAwayProgress;
    }

    public Vec3d getEffectCenter()
    {
        return effectCenter;
    }

    public void setEffectCenter(double x, double y, double z)
    {
        this.effectCenter = new Vec3d(x, y, z);
    }

    public float getCurrentScale()
    {
        return scaleInProgress;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(BOX_DEATH_TICKS, -1);
        this.getEntityData().define(DATA_EFFECT_ID, null);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (timeBoxWaiting == 0 && getDeathTicks() < 0)
        {
            PBEffect effect = getBoxEffect();

            if (effect == null)
            {
                if (level instanceof ServerWorld)
                {
                    remove();
                }
            }
            else
            {
                if (effect.isDone(this, effectTicksExisted))
                {
                    if (level instanceof ServerWorld)
                    {
                        boolean isCompletelyDone = true;

                        if (canGenerateMoreEffectsAfterwards && effect.canGenerateMoreEffectsAfterwards(this))
                        {
                            if (random.nextFloat() < PBConfig.boxLongevity)
                            {
                                startNewEffect();

                                isCompletelyDone = false;
                            }
                        }

                        if (isCompletelyDone)
                            startFadingOut();
                    }
                }
                else
                {
                    if (effectTicksExisted == 0)
                        setEffectCenter(getX(), getY(), getZ());

                    effect.doTick(this, effectCenter, effectTicksExisted);
                }
            }
        }

        if (timeBoxWaiting == 0)
        {
            setDeltaMovement(getDeltaMovement().scale(0.5));
        }
        else
        {
            setDeltaMovement(getDeltaMovement().scale(0.95));
        }

        if (floatAwayProgress >= 0.0f && floatAwayProgress < 1.0f)
        {
            float speed = MathHelper.square(floatAwayProgress - 0.7f);
            if (floatUp)
            {
                setDeltaMovement(getDeltaMovement().add(0, speed * 0.015f, 0));
            }
            else
            {
                moveRelative(0.4f, new Vec3d(-0.00f, speed * 0.02f, -0.02f));
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

        if (timeBoxWaiting == 0)
        {
            if (getDeathTicks() < 0)
            {
                if (!isInvisible() && level.isClientSide())
                {
                    double yCenter = getY() + this.getBbHeight() * 0.5;

                    for (int e = 0; e < 2; e++)
                    {
                        double xP = (random.nextDouble() - random.nextDouble()) * 0.2;
                        double yDir = random.nextDouble() * 0.1;
                        double zP = (random.nextDouble() - random.nextDouble()) * 0.2;

                        level.addParticle(ParticleTypes.SMOKE, getX() + xP, yCenter, getZ() + zP, 0.0D, yDir, 0.0D);
                    }
                    for (int e = 0; e < 3; e++)
                    {
                        double xDir = (random.nextDouble() - random.nextDouble()) * 3.0;
                        double yDir = random.nextDouble() * 4.0 + 2.0;
                        double zDir = (random.nextDouble() - random.nextDouble()) * 3.0;

                        double xP = (random.nextDouble() - 0.5) * getBbWidth();
                        double zP = (random.nextDouble() - 0.5) * getBbWidth();

                        level.addParticle(ParticleTypes.ENCHANT, getX() + xP + xDir, yCenter + yDir, getZ() + zP + zDir, -xDir, -yDir, -zDir);
                    }
                    for (int e = 0; e < 4; e++)
                    {
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
        }
        else
            timeBoxWaiting--;

        if (getDeathTicks() >= 0)
        {
            if (level instanceof ServerWorld)
            {
                if (getDeathTicks() >= 30)
                    remove();
            }
            else
            {
                for (int e = 0; e < Math.min(getDeathTicks(), 60); e++)
                {
                    double xP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double yP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double zP = (random.nextDouble() - random.nextDouble()) * 0.5;

                    level.addParticle(ParticleTypes.SMOKE, getX() + xP, getY() + yP, getZ() + zP, 0.0D, 0.0D, 0.0D);
                }
            }

            setDeathTicks(getDeathTicks() + 1);
        }
    }
    public void startNewEffect()
    {
        effectTicksExisted = 0;
        timeBoxWaiting = random.nextInt(40);

        boxEffect = ensureNotNull(PBECRegistry.createRandomEffect(level, random, effectCenter.x, effectCenter.y, effectCenter.z, true));
        entityData.set(DATA_EFFECT_ID, boxEffect);
    }

    public void startFadingOut()
    {
        setDeathTicks(0);
    }

    public void beginFloatingAway()
    {
        floatAwayProgress = 0.0f;
        floatUp = false;
    }

    public void beginFloatingUp()
    {
        floatAwayProgress = 0.0f;
        floatUp = true;
    }

    public void stopFloating()
    {
        floatAwayProgress = -1.0f;
        effectTicksExisted = 0;
    }

    public void beginScalingIn()
    {
        scaleInProgress = 0.0f;
    }

    public PBEffect getBoxEffect()
    {
        return entityData.get(DATA_EFFECT_ID);
    }

    public void setBoxEffect(PBEffect effect)
    {
        boxEffect = ensureNotNull(effect);
        entityData.set(DATA_EFFECT_ID, boxEffect);
    }
    public PBEffect ensureNotNull(PBEffect input) {
        while(input == null) {
            input = PBECRegistry.createRandomEffect(level, random, effectCenter.x, effectCenter.y, effectCenter.z, true);
        }
        return input;
    }

    public Random getRandom()
    {
        return random;
    }

    public int getDeathTicks()
    {
        return getEntityData().get(BOX_DEATH_TICKS);
    }

    public void setDeathTicks(int deathTicks)
    {
        getEntityData().set(BOX_DEATH_TICKS, deathTicks);
    }

    public float getRatioBoxOpen(float partialTicks)
    {
        if (floatAwayProgress >= 0.0f)
            return MathHelper.clamp(((floatAwayProgress + partialTicks * 0.025f - 0.5f) * 2.0f), 0.0f, 1.0f);
        else
            return 1.0f;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void push(Entity entityIn) {
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        readBoxData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        writeBoxData(compound);
    }

    public void readBoxData(CompoundNBT compound)
    {
        setBoxEffect(PBEffectRegistry.loadEffect(compound.getCompound("boxEffect")));

        effectTicksExisted = compound.getInt("effectTicksExisted");
        timeBoxWaiting = compound.getInt("timeBoxWaiting");
        canGenerateMoreEffectsAfterwards = compound.getBoolean("canGenerateMoreEffectsAfterwards");
        floatAwayProgress = compound.getFloat("floatAwayProgress");
        floatUp = compound.getBoolean("floatUp");
        scaleInProgress = compound.getFloat("scaleInProgress");

        if (compound.contains("effectCenterX", Constants.NBT.TAG_DOUBLE) && compound.contains("effectCenterY", Constants.NBT.TAG_DOUBLE) && compound.contains("effectCenterZ", Constants.NBT.TAG_DOUBLE))
            setEffectCenter(compound.getDouble("effectCenterX"), compound.getDouble("effectCenterY"), compound.getDouble("effectCenterZ"));
        else
            setEffectCenter(getX(), getY(), getZ());
    }

    public void writeBoxData(CompoundNBT compound)
    {
        CompoundNBT effectCompound = new CompoundNBT();
        PBEffectRegistry.writeEffect(getBoxEffect(), effectCompound);
        compound.put("boxEffect", effectCompound);

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
    public void writeUpdateData(PacketBuffer buffer, String context)
    {
        if (context.equals("boxEffect"))
        {
            CompoundNBT compound = new CompoundNBT();
            writeBoxData(compound);
            buffer.writeNbt(compound);
        }
    }

    @Override
    public void readUpdateData(PacketBuffer buffer, String context)
    {
        if (context.equals("boxEffect"))
        {
            CompoundNBT compound = buffer.readNbt();

            if (compound != null)
            {
                readBoxData(compound);
            }
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT compound = new CompoundNBT();
        writeBoxData(compound);
        buffer.writeNbt(compound);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        CompoundNBT compound = additionalData.readNbt();

        if (compound != null)
            readBoxData(compound);
    }
}
