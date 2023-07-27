package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 03.12.14.
 */
public class PBEffectDuplicateBox extends PBEffectNormal
{
    public static final int MODE_BOX_IN_BOX = 0;

    public int spawnMode;
    public PBEffectDuplicateBox() {

    }

    public static int timeNeededForSpawnMode(int mode)
    {
        if (mode == MODE_BOX_IN_BOX) {
            return 60;
        }

        return 0;
    }

    public PBEffectDuplicateBox(int spawnMode)
    {
        super(timeNeededForSpawnMode(spawnMode));
        this.spawnMode = spawnMode;
    }

    @Override
    public void setUpEffect(Level world, PandorasBoxEntity box, Vec3d effectCenter, RandomSource random)
    {
        if (world instanceof ServerLevel)
        {
            PBEffect effect = PBECRegistry.createRandomEffect(world, random, box.getX(), box.getY(), box.getZ(), true);
            PandorasBoxEntity newBox = Registry.Box.get().create(world);

            assert newBox != null;

            newBox.setBoxEffect(effect);
            newBox.setTimeBoxWaiting(40);
            newBox.moveTo(box.getX(), box.getY(), box.getZ(), box.getYRot(), box.getXRot());

            if (spawnMode == MODE_BOX_IN_BOX) {
                newBox.beginFloatingUp();
                newBox.beginScalingIn();
                world.addFreshEntity(newBox);
            }
        }
    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, float prevRatio, float newRatio)
    {

    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putInt("spawnMode", spawnMode);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        spawnMode = compound.getInt("spawnMode");
    }
}
