/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenCreativeTowers extends PBEffectGenerateByStructure {
    public PBEffectGenCreativeTowers() {}

    public PBEffectGenCreativeTowers(int maxTicksAlive)
    {
        super(maxTicksAlive);
    }

    public void createRandomStructures(RandomSource random, int number, double range, Collection<WeightedBlock> blocks) {
        this.structures = new Structure[number];
        for (int i = 0; i < number; i++)
        {
            structures[i] = createStructure();
            applyRandomProperties(structures[i], range, random);
            ((StructureCreativeTower) structures[i]).blocks = PandorasBoxHelper.getRandomBlockList(random, blocks);
        }
    }

    @Override
    public void generateStructure(Level level, PandorasBoxEntity entity, RandomSource random, Structure structure, BlockPos pos, float newRatio, float prevRatio) {
        StructureCreativeTower structureCreativeTower = (StructureCreativeTower) structure;

        int towerHeight = level.getMaxBuildHeight();
        int newY = Mth.floor(towerHeight * newRatio);
        int prevY = Mth.floor(towerHeight * prevRatio);

        for (int towerY = prevY; towerY < newY; towerY++) {
            Block block = structureCreativeTower.blocks[random.nextInt(structureCreativeTower.blocks.length)];

            setBlockVarying(level, new BlockPos(pos.getX() + structure.x, towerY, pos.getZ() + structure.z), block, structure.unifiedSeed);
        }
    }

    @Override
    public StructureCreativeTower createStructure()
    {
        return new StructureCreativeTower();
    }

    public static class StructureCreativeTower extends Structure {
        public Block[] blocks;

        public StructureCreativeTower() {
        }

        @Override
        public void writeToNBT(CompoundTag compound) {
            super.writeToNBT(compound);

            PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
        }

        @Override
        public void readFromNBT(CompoundTag compound) {
            super.readFromNBT(compound);

            blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
        }
    }
}
