package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectExplode extends PBEffectNormal
{
    public float explosionRadius;
    public boolean burning;

    public PBEffectExplode(int maxTicksAlive, float explosionRadius, boolean burning)
    {
        super(maxTicksAlive);
        this.explosionRadius = explosionRadius;
        this.burning = burning;
    }

    @Override
    public void doEffect(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, float prevRatio, float newRatio)
    {

    }

    @Override
    public void finalizeEffect(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random)
    {
        super.finalizeEffect(world, entity, effectCenter, random);

        if (world instanceof ServerWorld)
            world.explode(entity, entity.getX(), entity.getY(), entity.getZ(), explosionRadius, burning, Explosion.Mode.BREAK);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        explosionRadius = compound.getFloat("explosionRadius");
        burning = compound.getBoolean("burning");
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putFloat("explosionRadius", explosionRadius);
        compound.putBoolean("burning", burning);
    }
}
