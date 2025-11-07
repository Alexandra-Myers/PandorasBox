package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static ivorius.pandorasbox.effects.PBEffects.MELTDOWN_CREATORS;

/**
 * Created by Alexandra on 18.10.24.
 */
public final class PBEffectMeltdown extends PBEffect {
    public static final ResourceLocation MELTDOWN = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "render_meltdown");
    public static final MapCodec<PBEffectMeltdown> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(PBEffect.CODEC, () -> new PBEffect[0]).fieldOf("effects").forGetter(PBEffectMeltdown::getEffects),
                            PBNBTHelper.arrayCodec(Vec3.CODEC, () -> new Vec3[0]).fieldOf("effect_centers").forGetter(PBEffectMeltdown::getEffectCenters),
                            PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("effect_start_ticks").forGetter(PBEffectMeltdown::getEffectStartTicks),
                            Codec.FLOAT.fieldOf("range").forGetter(PBEffectMeltdown::getRange),
                            Codec.INT.fieldOf("max_ticks_alive").forGetter(PBEffectMeltdown::getMaxTicksAlive))
                    .apply(instance, PBEffectMeltdown::new));
    private PBEffect[] effects;
    private Vec3[] effectCenters;
    private Integer[] effectStartTicks;
    private final float range;
    private final int maxTicksAlive;
    private Integer indexToOverwrite = null;

    public PBEffectMeltdown(PBEffect[] effects, Vec3[] effectCenters, Integer[] effectStartTicks, float range, int maxTicksAlive) {
        this.effects = effects;
        this.effectCenters = effectCenters;
        this.effectStartTicks = effectStartTicks;
        this.range = range;
        this.maxTicksAlive = maxTicksAlive;
    }

    public PBEffectMeltdown(PBEffect firstEffect, float range, int maxTicksAlive) {
        this.effects = new PBEffect[] {firstEffect};
        this.effectCenters = new Vec3[] {Vec3.ZERO};
        this.effectStartTicks = new Integer[] {0};
        this.range = range;
        this.maxTicksAlive = maxTicksAlive;
    }

    public PBEffect[] getEffects() {
        return effects;
    }

    public Vec3[] getEffectCenters() {
        return effectCenters;
    }

    public Integer[] getEffectStartTicks() {
        return effectStartTicks;
    }

    public float getRange() {
        return range;
    }

    public int getMaxTicksAlive() {
        return maxTicksAlive;
    }

    @Override
    public void doTick(PandorasBoxEntity entity, Vec3 effectCenter, int ticksAlive) {
        Level level = entity.level();
        RandomSource random = entity.getRandom();
        int rand;
        if (ticksAlive == 0) {
            double xP = (random.nextDouble() - 0.5) * range;
            double yP = (random.nextDouble() - 0.5) * range * 0.25;
            double zP = (random.nextDouble() - 0.5) * range;
            Vec3 newEffectCenter = effectCenter.add(xP, yP, zP);
            effectCenters[0] = newEffectCenter;
            effectStartTicks[0] = 5;
        }
        rand = random.nextInt(MELTDOWN_CREATORS.length * 16);
        if (!level.isClientSide() && rand < MELTDOWN_CREATORS.length) {
            double xP = (random.nextDouble() - 0.5) * range;
            double yP = (random.nextDouble() - 0.5) * range * 0.25;
            double zP = (random.nextDouble() - 0.5) * range;
            Vec3 newEffectCenter = effectCenter.add(xP, yP, zP);
            PBEffect pbEffect = MELTDOWN_CREATORS[rand].constructEffect(level, newEffectCenter.x, newEffectCenter.y, newEffectCenter.z, random);
            if (indexToOverwrite != null) {
                effects[indexToOverwrite] = pbEffect;
                effectCenters[indexToOverwrite] = newEffectCenter;
                effectStartTicks[indexToOverwrite] = ticksAlive + 5;
                indexToOverwrite = null;
            } else {
                effects = Arrays.copyOf(effects, effects.length + 1);
                effects[effects.length - 1] = pbEffect;
                effectCenters = Arrays.copyOf(effectCenters, effectCenters.length + 1);
                effectCenters[effectCenters.length - 1] = newEffectCenter;
                effectStartTicks = Arrays.copyOf(effectStartTicks, effectStartTicks.length + 1);
                effectStartTicks[effectStartTicks.length - 1] = ticksAlive + 5;
            }
            entity.setBoxEffect(this);
        }
        for (int i = 0; i < effects.length; i++) {
            int ticksForEffect = ticksAlive - effectStartTicks[i];
            Vec3 currentCenter = effectCenters[i];
            if (ticksForEffect < 5) {
                if (level.isClientSide()) {
                    Vec3 baseDiff = currentCenter.subtract(effectCenter);
                    Vec3 delta = baseDiff.normalize().scale(2.5);
                    for (int e = 0; e < 50; e++) {
                        double speedFactor = 1 + (random.nextDouble() - 0.5);
                        double xDir = delta.x * speedFactor;
                        double yDir = delta.y * speedFactor;
                        double zDir = delta.z * speedFactor;
                        double xP = (random.nextDouble() - 0.5) * entity.getBbWidth() * 2;
                        double yP = (random.nextDouble() - 0.5) * entity.getBbHeight() * 2;
                        double zP = (random.nextDouble() - 0.5) * entity.getBbWidth() * 2;

                        level.addParticle(ParticleTypes.SMOKE, effectCenter.x + xP, effectCenter.y + yP, effectCenter.z + zP, -xDir, -yDir, -zDir);
                    }
                    for (int e = 0; e < 10; e++) {
                        double speedFactor = 1 + (random.nextDouble() - 0.5);
                        double xDir = delta.x * speedFactor;
                        double yDir = delta.y * speedFactor;
                        double zDir = delta.z * speedFactor;
                        double xP = (random.nextDouble() - 0.5) * entity.getBbWidth() / 2;
                        double yP = (random.nextDouble() - 0.5) * entity.getBbHeight() / 2;
                        double zP = (random.nextDouble() - 0.5) * entity.getBbWidth() / 2;

                        level.addParticle(ParticleTypes.FLAME, effectCenter.x + xP, effectCenter.y + yP, effectCenter.z + zP, -xDir, -yDir, -zDir);
                    }
                    for (int e = 0; e < 10; e++) {
                        double speedFactor = 1 + (random.nextDouble() - 0.5);
                        double xDir = delta.x * speedFactor;
                        double yDir = delta.y * speedFactor;
                        double zDir = delta.z * speedFactor;
                        double xP = (random.nextDouble() - 0.5) * entity.getBbWidth() / 2;
                        double yP = (random.nextDouble() - 0.5) * entity.getBbHeight() / 2;
                        double zP = (random.nextDouble() - 0.5) * entity.getBbWidth() / 2;

                        level.addParticle(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1), effectCenter.x + xP, effectCenter.y + yP, effectCenter.z + zP, -xDir, -yDir, -zDir);
                    }
                }
            }
            if (effects[i].isDone(ticksForEffect)) {
                if (indexToOverwrite == null) indexToOverwrite = i;
                continue;
            }
            effects[i].doTick(entity, currentCenter, ticksForEffect);
            if (level.isClientSide()) {
                if (ticksForEffect >= 0 && ticksForEffect < 5) {
                    for (int e = 0; e < 300; e++) {
                        double xDir = (random.nextDouble() - random.nextDouble()) * 2.0;
                        double yDir = random.nextDouble() + 2.0;
                        double zDir = (random.nextDouble() - random.nextDouble()) * 2.0;

                        double xP = (random.nextDouble() - 0.5) * entity.getBbWidth();
                        double zP = (random.nextDouble() - 0.5) * entity.getBbWidth();

                        level.addParticle(ParticleTypes.ENCHANT, currentCenter.x + xP + xDir, currentCenter.y + yDir, currentCenter.z + zP + zDir, -xDir, -yDir, -zDir);
                    }
                    for (int e = 0; e < 400; e++) {
                        double xP = (random.nextDouble() - random.nextDouble()) * 0.5;
                        double yP = (random.nextDouble() - random.nextDouble()) * 0.5;
                        double zP = (random.nextDouble() - random.nextDouble()) * 0.5;

                        double xDir = (random.nextDouble() * 3D) - 1.75D;
                        double yDir = (random.nextDouble() * 3D) - 1.75D;
                        double zDir = (random.nextDouble() * 3D) - 1.75D;

                        level.addParticle(ParticleTypes.PORTAL, currentCenter.x + xP, currentCenter.y + yP, currentCenter.z + zP, xDir, yDir, zDir);
                    }
                }
                for (int e = 0; e < Math.min(ticksForEffect, 45); e++) {
                    double xP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double yP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double zP = (random.nextDouble() - random.nextDouble()) * 0.5;

                    double xO = currentCenter.x + xP;
                    double zO = currentCenter.z + zP;

                    double xDif = currentCenter.x - xO;
                    double zDif = currentCenter.z - zO;

                    level.addParticle(ParticleTypes.SMOKE, xO, currentCenter.y + yP, zO, random.nextDouble() * xDif, random.nextDouble() * 0.2, random.nextDouble() * zDif);
                }
                for (int e = 0; e < Math.min(ticksForEffect, 30); e++) {
                    double xP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double yP = (random.nextDouble() - random.nextDouble()) * 0.5;
                    double zP = (random.nextDouble() - random.nextDouble()) * 0.5;

                    double xO = currentCenter.x + xP;
                    double zO = currentCenter.z + zP;

                    double xDif = currentCenter.x - xO;
                    double zDif = currentCenter.z - zO;

                    level.addParticle(ParticleTypes.FLAME, xO, currentCenter.y + yP, zO, random.nextDouble() * xDif, random.nextDouble() * 0.4, random.nextDouble() * zDif);
                }
            }
        }
        if (!entity.isInvisible() && level.isClientSide()) {
            for (int e = 0; e < Math.min(ticksAlive, 20); e++) {
                double xP = (random.nextDouble() - random.nextDouble()) * 0.5;
                double yP = (random.nextDouble() - random.nextDouble()) * 0.5;
                double zP = (random.nextDouble() - random.nextDouble()) * 0.5;

                double xO = entity.getX() + xP;
                double zO = entity.getZ() + zP;

                double xDif = entity.getX() - xO;
                double zDif = entity.getZ() - zO;

                level.addParticle(ParticleTypes.FLAME, xO, entity.getY() + yP, zO, random.nextDouble() * xDif, random.nextDouble() * 0.4, random.nextDouble() * zDif);
            }
        }
        if (!level.isClientSide() && ticksAlive == maxTicksAlive - 1)
            level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), 10, true, Level.ExplosionInteraction.MOB);
    }

    @Override
    public boolean isDone(int ticksAlive) {
        return ticksAlive >= maxTicksAlive;
    }

    @Override
    public boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity) {
        return false;
    }

    @Override
    public int getTicksExistedForEffect(PBEffect identityEffect, int ticksAlive) {
        for (int i = 0; i < effects.length; i++) {
            if (effects[i] == identityEffect) {
                return ticksAlive - effectStartTicks[i];
            }
        }
        return -1;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation rendererResourceLocationForEffect() {
        return MELTDOWN;
    }
}
