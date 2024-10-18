package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.EntityInit;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 03.12.14.
 */
public class PBEffectDuplicateBox extends PBEffectNormal {
    public static final int MODE_BOX_IN_BOX = 0;

    public int spawnMode;
    public PBEffectDuplicateBox() {

    }

    public static int timeNeededForSpawnMode(int mode) {
        if (mode == MODE_BOX_IN_BOX) {
            return 60;
        }

        return 0;
    }

    public PBEffectDuplicateBox(int spawnMode) {
        super(timeNeededForSpawnMode(spawnMode));
        this.spawnMode = spawnMode;
    }

    @Override
    public void setUpEffect(Level level, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random) {
        if (!level.isClientSide) {
            PBEffect effect = PBECRegistry.createRandomEffect(level, random, box.getX(), box.getY(), box.getZ(), true);
            PandorasBoxEntity newBox = new PandorasBoxEntity(EntityInit.BOX, level, true, true);

            newBox.setBoxEffect(effect);
            newBox.setBoxWaitingTime(40);
            newBox.moveTo(box.getX(), box.getY(), box.getZ(), box.getYRot(), box.getXRot());

            if (spawnMode == MODE_BOX_IN_BOX) {
                newBox.beginFloating();
                newBox.beginScalingIn();
                level.addFreshEntity(newBox);
            }
        }
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {

    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putInt("spawnMode", spawnMode);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        spawnMode = compound.getInt("spawnMode");
    }
}
