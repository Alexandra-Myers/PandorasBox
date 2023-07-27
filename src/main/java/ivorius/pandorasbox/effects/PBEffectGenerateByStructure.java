/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerateByStructure extends PBEffectNormal
{
    public Structure[] structures;
    public PBEffectGenerateByStructure() {}

    public PBEffectGenerateByStructure(int maxTicksAlive)
    {
        super(maxTicksAlive);
        structures = new Structure[0];
    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio)
    {
        for (Structure structure : structures)
        {
            float newStructureRatio = getStructureRatio(newRatio, structure);
            float prevStructureRatio = getStructureRatio(prevRatio, structure);

            int baseX = Mth.floor(effectCenter.x);
            int baseY = Mth.floor(effectCenter.y);
            int baseZ = Mth.floor(effectCenter.z);

            if (newStructureRatio > prevStructureRatio)
            {
                generateStructure(world, entity, random, structure, new BlockPos(baseX, baseY, baseZ), newStructureRatio, prevStructureRatio);
            }
        }
    }

    private float getStructureRatio(float ratio, Structure structure)
    {
        return Mth.clamp((ratio - structure.structureStart) / structure.structureLength, 0.0f, 1.0f);
    }

    public abstract void generateStructure(Level world, PandorasBoxEntity entity, RandomSource random, Structure structure, BlockPos pos, float newRatio, float prevRatio);

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        ListTag structureTagList = new ListTag();
        for (Structure structure : structures)
        {
            CompoundTag structureCompound = new CompoundTag();
            structure.writeToNBT(structureCompound);
            structureTagList.add(structureCompound);
        }
        compound.put("structures", structureTagList);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        ListTag structureTagList = compound.getList("structures", 10);
        structures = new Structure[structureTagList.size()];
        for (int i = 0; i < structures.length; i++)
        {
            structures[i] = createStructure(structureTagList.getCompound(i));
        }
    }

    public abstract Structure createStructure();

    public Structure createStructure(CompoundTag compound)
    {
        Structure structure = createStructure();
        structure.readFromNBT(compound);
        return structure;
    }

    public static void applyRandomProperties(Structure structure, double range, RandomSource random)
    {
        structure.structureLength = random.nextFloat() * 0.8f + 0.1f;
        structure.structureStart = random.nextFloat() * (1.0f - structure.structureLength);

        structure.x = Mth.floor((random.nextDouble() - random.nextDouble()) * range);
        structure.y = Mth.floor((random.nextDouble() - random.nextDouble()) * range);
        structure.z = Mth.floor((random.nextDouble() - random.nextDouble()) * range);

        structure.unifiedSeed = PandorasBoxHelper.getRandomUnifiedSeed(random);
    }

    public static class Structure
    {
        public float structureStart;
        public float structureLength;

        public int x;
        public int y;
        public int z;

        public int unifiedSeed;

        public Structure()
        {

        }

        public Structure(float structureStart, float structureLength, int x, int y, int z, int unifiedSeed)
        {
            this.structureStart = structureStart;
            this.structureLength = structureLength;
            this.x = x;
            this.y = y;
            this.z = z;
            this.unifiedSeed = unifiedSeed;
        }

        public void writeToNBT(CompoundTag compound)
        {
            compound.putFloat("structureStart", structureStart);
            compound.putFloat("structureLength", structureLength);

            compound.putInt("x", x);
            compound.putInt("y", y);
            compound.putInt("z", z);
            compound.putInt("unifiedSeed", unifiedSeed);
        }

        public void readFromNBT(CompoundTag compound)
        {
            structureStart = compound.getFloat("structureStart");
            structureLength = compound.getFloat("structureLength");

            x = compound.getInt("x");
            y = compound.getInt("y");
            z = compound.getInt("z");
            unifiedSeed = compound.getInt("unifiedSeed");
        }
    }
}
