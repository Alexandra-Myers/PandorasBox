package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 03.12.14.
 */
public class PBEffectDuplicateBox extends PBEffectNormal
{
    public static final int MODE_BOX_IN_BOX = 0;

    public int spawnMode;

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
    public void setUpEffect(World world, PandorasBoxEntity box, Vec3d effectCenter, Random random)
    {
        if (world instanceof ServerWorld)
        {
            PBEffect effect = PBECRegistry.createRandomEffect(world, random, box.getX(), box.getY(), box.getZ(), true);
            PandorasBoxEntity newBox = Registry.Box.get().create(world);

            assert newBox != null;

            newBox.setBoxEffect(effect);
            newBox.setTimeBoxWaiting(40);
            newBox.moveTo(box.getX(), box.getY(), box.getZ(), box.yRot, box.xRot);

            if (spawnMode == MODE_BOX_IN_BOX) {
                newBox.beginFloatingUp();
                newBox.beginScalingIn();
                world.addFreshEntity(newBox);
            }
        }
    }

    @Override
    public void doEffect(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, float prevRatio, float newRatio)
    {

    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putInt("spawnMode", spawnMode);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        spawnMode = compound.getInt("spawnMode");
    }
}
