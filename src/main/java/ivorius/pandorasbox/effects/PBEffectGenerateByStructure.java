/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerateByStructure extends PBEffectNormal
{
    public Structure[] structures;

    public PBEffectGenerateByStructure(int maxTicksAlive)
    {
        super(maxTicksAlive);
        structures = new Structure[0];
    }

    @Override
    public void doEffect(World world, EntityPandorasBox entity, Vec3d effectCenter, Random random, float prevRatio, float newRatio)
    {
        for (Structure structure : structures)
        {
            float newStructureRatio = getStructureRatio(newRatio, structure);
            float prevStructureRatio = getStructureRatio(prevRatio, structure);

            int baseX = MathHelper.floor(effectCenter.x);
            int baseY = MathHelper.floor(effectCenter.y);
            int baseZ = MathHelper.floor(effectCenter.z);

            if (newStructureRatio > prevStructureRatio)
            {
                generateStructure(world, entity, random, structure, new BlockPos(baseX, baseY, baseZ), newStructureRatio, prevStructureRatio);
            }
        }
    }

    private float getStructureRatio(float ratio, Structure structure)
    {
        return MathHelper.clamp((ratio - structure.structureStart) / structure.structureLength, 0.0f, 1.0f);
    }

    public abstract void generateStructure(World world, EntityPandorasBox entity, Random random, Structure structure, BlockPos pos, float newRatio, float prevRatio);

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        ListNBT structureTagList = new ListNBT();
        for (Structure structure : structures)
        {
            CompoundNBT structureCompound = new CompoundNBT();
            structure.writeToNBT(structureCompound);
            structureTagList.add(structureCompound);
        }
        compound.put("structures", structureTagList);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        ListNBT structureTagList = compound.getList("structures", Constants.NBT.TAG_COMPOUND);
        structures = new Structure[structureTagList.size()];
        for (int i = 0; i < structures.length; i++)
        {
            structures[i] = createStructure(structureTagList.getCompound(i));
        }
    }

    public abstract Structure createStructure();

    public Structure createStructure(CompoundNBT compound)
    {
        Structure structure = createStructure();
        structure.readFromNBT(compound);
        return structure;
    }

    public static void applyRandomProperties(Structure structure, double range, Random random)
    {
        structure.structureLength = random.nextFloat() * 0.8f + 0.1f;
        structure.structureStart = random.nextFloat() * (1.0f - structure.structureLength);

        structure.x = MathHelper.floor((random.nextDouble() - random.nextDouble()) * range);
        structure.y = MathHelper.floor((random.nextDouble() - random.nextDouble()) * range);
        structure.z = MathHelper.floor((random.nextDouble() - random.nextDouble()) * range);

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

        public void writeToNBT(CompoundNBT compound)
        {
            compound.putFloat("structureStart", structureStart);
            compound.putFloat("structureLength", structureLength);

            compound.putInt("x", x);
            compound.putInt("y", y);
            compound.putInt("z", z);
            compound.putInt("unifiedSeed", unifiedSeed);
        }

        public void readFromNBT(CompoundNBT compound)
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
