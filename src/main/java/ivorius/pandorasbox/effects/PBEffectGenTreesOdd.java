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
import net.minecraft.world.level.levelgen.feature.TreeFeature;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTreesOdd extends PBEffectGenerateByGenerator
{
    public static final int treeJungle = 0;
    public PBEffectGenTreesOdd() {}

    public Block trunkBlock;
    public Block leafBlock;

    private ArrayListExtensions<TreeFeature> treeGens;

    public PBEffectGenTreesOdd(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags, Block trunkBlock, Block leafBlock)
    {
        super(time, range, unifiedSeed, requiresSolidGround, chancePerBlock, generatorFlags);
        this.trunkBlock = trunkBlock;
        this.leafBlock = leafBlock;
        initializeGens();
    }

    private void initializeGens()
    {
        treeGens = new ArrayListExtensions<>();
        treeGens.add(0, (TreeFeature) PandorasBox.instance.MEGA_JUNGLE);
        ((MegaTreeFeature)treeGens.get(0)).setLeaves(leafBlock.defaultBlockState());
        ((MegaTreeFeature)treeGens.get(0)).setTrunk(trunkBlock.defaultBlockState());
    }

    @Override
    public List<TreeFeature> getGenerators()
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
