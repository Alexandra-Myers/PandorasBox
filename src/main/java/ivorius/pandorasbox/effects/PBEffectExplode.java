package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectExplode extends PBEffectNormal {
    public Level.ExplosionInteraction interaction;
    public float explosionRadius;
    public boolean burning;
    public PBEffectExplode() {

    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {

    }

    public PBEffectExplode(int maxTicksAlive, float explosionRadius, boolean burning, Level.ExplosionInteraction interaction) {
        super(maxTicksAlive);
        this.explosionRadius = explosionRadius;
        this.burning = burning;
        this.interaction = interaction;
    }

    @Override
    public void finalizeEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        super.finalizeEffect(level, entity, effectCenter, random);

        if (!level.isClientSide)
            level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), explosionRadius, burning, interaction);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        explosionRadius = compound.getFloat("explosionRadius");
        burning = compound.getBoolean("burning");
        interaction = Level.ExplosionInteraction.values()[compound.getInt("interaction")];
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putFloat("explosionRadius", explosionRadius);
        compound.putBoolean("burning", burning);
        compound.putInt("interaction", interaction.ordinal());
    }
}
