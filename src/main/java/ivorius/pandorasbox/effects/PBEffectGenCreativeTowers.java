/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenCreativeTowers extends PBEffectGenerateByStructure
{

    public PBEffectGenCreativeTowers(int maxTicksAlive)
    {
        super(maxTicksAlive);
    }

    public void createRandomStructures(Random random, int number, double range, Collection<WeightedBlock> blocks)
    {
        this.structures = new Structure[number];
        for (int i = 0; i < number; i++)
        {
            structures[i] = createStructure();
            applyRandomProperties(structures[i], range, random);
            ((StructureCreativeTower) structures[i]).blocks = PandorasBoxHelper.getRandomBlockList(random, blocks);
        }
    }

    @Override
    public void generateStructure(World world, EntityPandorasBox entity, Random random, Structure structure, BlockPos pos, float newRatio, float prevRatio)
    {
        if (world instanceof ServerWorld)
        {
            StructureCreativeTower structureCreativeTower = (StructureCreativeTower) structure;

            int towerHeight = world.getMaxBuildHeight();
            int newY = MathHelper.floor(towerHeight * newRatio);
            int prevY = MathHelper.floor(towerHeight * prevRatio);

            for (int towerY = prevY; towerY < newY; towerY++)
            {
                Block block = structureCreativeTower.blocks[random.nextInt(structureCreativeTower.blocks.length)];

                setBlockVarying(world, new BlockPos(pos.getX() + structure.x, towerY, pos.getZ() + structure.z), block, structure.unifiedSeed);
            }
        }
    }

    @Override
    public StructureCreativeTower createStructure()
    {
        return new StructureCreativeTower();
    }

    public static class StructureCreativeTower extends Structure
    {
        public Block[] blocks;

        public StructureCreativeTower()
        {
        }

        @Override
        public void writeToNBT(CompoundNBT compound)
        {
            super.writeToNBT(compound);

            PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
        }

        @Override
        public void readFromNBT(CompoundNBT compound)
        {
            super.readFromNBT(compound);

            blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
        }
    }
}
