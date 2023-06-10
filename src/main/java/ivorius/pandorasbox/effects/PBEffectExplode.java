package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectExplode extends PBEffectNormal
{
    public float explosionRadius;
    public boolean burning;
    public PBEffectExplode() {

    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, float prevRatio, float newRatio) {

    }

    public PBEffectExplode(int maxTicksAlive, float explosionRadius, boolean burning)
    {
        super(maxTicksAlive);
        this.explosionRadius = explosionRadius;
        this.burning = burning;
    }

    @Override
    public void finalizeEffect(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random)
    {
        super.finalizeEffect(world, entity, effectCenter, random);

        if (world instanceof ServerLevel)
            world.explode(entity, entity.getX(), entity.getY(), entity.getZ(), explosionRadius, burning, Level.ExplosionInteraction.TNT);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        explosionRadius = compound.getFloat("explosionRadius");
        burning = compound.getBoolean("burning");
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putFloat("explosionRadius", explosionRadius);
        compound.putBoolean("burning", burning);
    }
}
