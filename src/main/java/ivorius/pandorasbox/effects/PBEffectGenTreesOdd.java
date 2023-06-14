/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.worldgen.MegaTreeFeature;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTreesOdd extends PBEffectGenerateByGenerator<MegaTreeFeature>
{
    public static final int treeJungle = 0;
    public PBEffectGenTreesOdd() {}

    public Block trunkBlock;
    public Block leafBlock;

    public PBEffectGenTreesOdd(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags, Block trunkBlock, Block leafBlock)
    {
        super(time, range, unifiedSeed, requiresSolidGround, chancePerBlock, generatorFlags);
        this.trunkBlock = trunkBlock;
        this.leafBlock = leafBlock;
    }

    @Override
    public ArrayListExtensions<MegaTreeFeature> initializeGens()
    {
        ArrayListExtensions<MegaTreeFeature> trees = new ArrayListExtensions<>();
        trees.add(treeJungle, (MegaTreeFeature) PandorasBox.instance.MEGA_JUNGLE);
        trees.get(treeJungle).setLeaves(leafBlock.defaultBlockState());
        trees.get(treeJungle).setTrunk(trunkBlock.defaultBlockState());
        return trees;
    }

    @Override
    public List<MegaTreeFeature> getGenerators()
    {
        return treeGens;
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putString("trunkBlock", PBNBTHelper.storeBlockString(trunkBlock));
        compound.putString("leafBlock", PBNBTHelper.storeBlockString(leafBlock));
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        trunkBlock = PBNBTHelper.getBlock(compound.getString("trunkBlock"));
        leafBlock = PBNBTHelper.getBlock(compound.getString("leafBlock"));

        initializeGens();
    }
}
