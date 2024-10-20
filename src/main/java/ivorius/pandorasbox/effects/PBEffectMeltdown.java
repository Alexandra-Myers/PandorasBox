package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

import static ivorius.pandorasbox.effects.PBEffects.MELTDOWN_CREATORS;

/**
 * Created by Alexandra on 18.10.24.
 */
public final class PBEffectMeltdown extends PBEffect {
    public PBEffect[] effects;
    public float range;
    public int maxTicksAlive;
    private Vec3[] effectCenters;
    public int[] effectStartTicks;

    public PBEffectMeltdown() {
        this.effects = new PBEffect[1];
        this.effectCenters = new Vec3[] {Vec3.ZERO};
        this.effectStartTicks = new int[] {0};
    }

    public PBEffectMeltdown(PBEffect firstEffect, float range, int maxTicksAlive) {
        this.effects = new PBEffect[] {firstEffect};
        this.effectCenters = new Vec3[] {Vec3.ZERO};
        this.effectStartTicks = new int[] {0};
        this.range = range;
        this.maxTicksAlive = maxTicksAlive;
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
            effectStartTicks[0] = 0;
        }
        rand = random.nextInt(MELTDOWN_CREATORS.length * 32);
        if (rand < MELTDOWN_CREATORS.length) {
            double xP = (random.nextDouble() - 0.5) * range;
            double yP = (random.nextDouble() - 0.5) * range * 0.25;
            double zP = (random.nextDouble() - 0.5) * range;
            Vec3 newEffectCenter = effectCenter.add(xP, yP, zP);
            PBEffect pbEffect = MELTDOWN_CREATORS[rand].constructEffect(level, newEffectCenter.x, newEffectCenter.y, newEffectCenter.z, random);
            effects = Arrays.copyOf(effects, effects.length + 1);
            effects[effects.length - 1] = pbEffect;
            effectCenters = Arrays.copyOf(effectCenters, effectCenters.length + 1);
            effectCenters[effectCenters.length - 1] = newEffectCenter;
            effectStartTicks = Arrays.copyOf(effectStartTicks, effectStartTicks.length + 1);
            effectStartTicks[effectStartTicks.length - 1] = ticksAlive;
        }
        for (int i = 0; i < effects.length; i++) {
            int ticksForEffect = ticksAlive - effectStartTicks[i];
            if (effects[i].isDone(entity, ticksForEffect)) continue;
            Vec3 currentCenter = effectCenters[i];
            effects[i].doTick(entity, currentCenter, ticksForEffect);
            if (level.isClientSide) {
                if (ticksForEffect == 0) {
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
        if (!entity.isInvisible() && level.isClientSide) {
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
        if (ticksAlive == maxTicksAlive - 1)
            level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), 10, true, Level.ExplosionInteraction.MOB);
    }

    @Override
    public boolean isDone(PandorasBoxEntity entity, int ticksAlive) {
        return ticksAlive >= maxTicksAlive;
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        ListTag list = new ListTag();

        for (int i = 0; i < effects.length; i++) {
            CompoundTag cmp = new CompoundTag();

            cmp.putString("pbEffectID", effects[i].getEffectID());
            CompoundTag effectCmp = new CompoundTag();
            effects[i].writeToNBT(effectCmp, registryAccess);
            cmp.put("pbEffectCompound", effectCmp);
            Vec3 effectCenter = effectCenters[i];
            cmp.putDouble("x", effectCenter.x);
            cmp.putDouble("y", effectCenter.y);
            cmp.putDouble("z", effectCenter.z);
            cmp.putInt("startTicks", effectStartTicks[i]);

            list.add(cmp);
        }

        compound.put("effects", list);
        compound.putFloat("range", range);
        compound.putInt("maxTicksAlive", maxTicksAlive);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        ListTag list = compound.getList("effects", 10);

        effects = new PBEffect[list.size()];
        effectCenters = new Vec3[list.size()];
        effectStartTicks = new int[list.size()];

        for (int i = 0; i < effects.length; i++) {
            CompoundTag cmp = list.getCompound(i);

            effects[i] = PBEffectRegistry.loadEffect(cmp.getString("pbEffectID"), cmp.getCompound("pbEffectCompound"), registryAccess);
            double x = cmp.getDouble("x");
            double y = cmp.getDouble("y");
            double z = cmp.getDouble("z");
            effectCenters[i] = new Vec3(x, y, z);
            effectStartTicks[i] = cmp.getInt("startTicks");
        }
        range = compound.getFloat("range");
        maxTicksAlive = compound.getInt("maxTicksAlive");
    }

    @Override
    public boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity) {
        return false;
    }
}
