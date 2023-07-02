/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.worldgen.MegaTreeFeature;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.feature.TreeFeature;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTreesOdd extends PBEffectGenerateByGenerator<MegaTreeFeature>
{
    public PBEffectGenTreesOdd() {

    }

    public Block trunkBlock;
    public Block leafBlock;

    public PBEffectGenTreesOdd(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags, Block trunkBlock, Block leafBlock)
    {
        super(time, range, unifiedSeed, requiresSolidGround, chancePerBlock, generatorFlags);
        this.trunkBlock = trunkBlock;
        this.leafBlock = leafBlock;
        this.treeGens = initializeGens();
    }

    @Override
    public ArrayListExtensions<MegaTreeFeature> initializeGens()
    {
        ArrayListExtensions<MegaTreeFeature> trees = new ArrayListExtensions<>();
        trees.add(0, (MegaTreeFeature) PandorasBox.instance.MEGA_JUNGLE);
        trees.get(0).setLeaves(leafBlock.defaultBlockState());
        trees.get(0).setTrunk(trunkBlock.defaultBlockState());
        return trees;
    }

    @Override
    public List<MegaTreeFeature> getGenerators()
    {
        return treeGens;
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putString("trunkBlock", PBNBTHelper.storeBlockString(trunkBlock));
        compound.putString("leafBlock", PBNBTHelper.storeBlockString(leafBlock));
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        trunkBlock = PBNBTHelper.getBlock(compound.getString("trunkBlock"));
        leafBlock = PBNBTHelper.getBlock(compound.getString("leafBlock"));

        initializeGens();
    }
}
