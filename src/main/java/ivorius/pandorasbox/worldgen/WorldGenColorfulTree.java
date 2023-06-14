/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.worldgen;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import java.util.List;
import java.util.Random;

public class WorldGenColorfulTree extends TreeFeature implements AccessibleTreeFeature
{
    public Block trunk;
    public int[] metas;
    public Block soil;

    private Random random;
    private Level level;
    private BlockPos origin;
    int heightLimit;
    int height;
    double heightAttenuation;
    double field_175944_d;
    double field_175945_e;
    double leafDensity;
    int spread;
    int maxHeight;
    /**
     * Sets the distance limit for how far away the generator will populate leaves from the base leaf node.
     */
    int leafDistanceLimit;
    List<FoliageCoordinates> foliageCoords;

    public WorldGenColorfulTree(Codec<TreeConfiguration> configIn, int height)
    {
        super(configIn);
        this.origin = BlockPos.ZERO;
        this.heightAttenuation = 0.618D;
        this.field_175944_d = 0.381D;
        this.field_175945_e = 1.0D;
        this.leafDensity = 1.0D;
        this.spread = 1;
        this.maxHeight = height;
        this.leafDistanceLimit = 4;
    }

    /**
     * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
     */
    void generateLeafNodeList()
    {
        this.height = (int) ((double) this.heightLimit * this.heightAttenuation);

        if (this.height >= this.heightLimit)
        {
            this.height = this.heightLimit - 1;
        }

        int i = (int) (1.382D + Math.pow(this.leafDensity * (double) this.heightLimit / 13.0D, 2.0D));

        if (i < 1)
        {
            i = 1;
        }

        int j = this.origin.getY() + this.height;
        int k = this.heightLimit - this.leafDistanceLimit;
        this.foliageCoords = Lists.newArrayList();
        this.foliageCoords.add(new FoliageCoordinates(this.origin.above(k), j));

        for (; k >= 0; --k)
        {
            float f = this.layerSize(k);

            if (f >= 0.0F)
            {
                for (int l = 0; l < i; ++l)
                {
                    double d0 = this.field_175945_e * (double) f * ((double) this.random.nextFloat() + 0.328D);
                    double d1 = (double) (this.random.nextFloat() * 2.0F) * Math.PI;
                    double d2 = d0 * Math.sin(d1) + 0.5D;
                    double d3 = d0 * Math.cos(d1) + 0.5D;
                    BlockPos blockpos = this.origin.offset(BlockPos.containing(d2, k - 1, d3));
                    BlockPos blockpos1 = blockpos.above(this.leafDistanceLimit);

                    this.func_175936_a(blockpos, blockpos1);
                    int i1 = this.origin.getX() - blockpos.getX();
                    int j1 = this.origin.getZ() - blockpos.getZ();
                    double d4 = (double) blockpos.getY() - Math.sqrt(i1 * i1 + j1 * j1) * this.field_175944_d;
                    int k1 = (int) Math.min(d4, j);
                    BlockPos blockpos2 = new BlockPos(this.origin.getX(), k1, this.origin.getZ());

                    this.func_175936_a(blockpos2, blockpos);
                    this.foliageCoords.add(new FoliageCoordinates(blockpos, blockpos2.getY()));
                }
            }
        }
    }

    void createLeaves(BlockPos blockPos, float f, BlockState blockState)
    {
        int i = (int) ((double) f + 0.618D);

        for (int j = -i; j <= i; ++j)
        {
            for (int k = -i; k <= i; ++k)
            {
                if (Math.pow((double) Math.abs(j) + 0.5D, 2.0D) + Math.pow((double) Math.abs(k) + 0.5D, 2.0D) <= (double) (f * f))
                {
                    BlockPos blockpos1 = blockPos.offset(j, 0, k);
                    BlockState state = this.level.getBlockState(blockpos1);

                    if (state.isAir() || state.is(BlockTags.LEAVES))
                    {
                        this.setBlock(this.level, blockpos1, blockState);
                    }
                }
            }
        }
    }

    /**
     * Gets the rough size of a layer of the tree.
     */
    float layerSize(int p_76490_1_)
    {
        if ((float) p_76490_1_ < (float) this.heightLimit * 0.3F)
        {
            return -1.0F;
        }
        else
        {
            float f = (float) this.heightLimit / 2.0F;
            float f1 = f - (float) p_76490_1_;
            float f2 = Mth.sqrt(f * f - f1 * f1);

            if (f1 == 0.0F)
            {
                f2 = f;
            }
            else if (Math.abs(f1) >= f)
            {
                return 0.0F;
            }

            return f2 * 0.5F;
        }
    }

    float leafSize(int p_76495_1_)
    {
        return p_76495_1_ >= 0 && p_76495_1_ < this.leafDistanceLimit ? (p_76495_1_ != 0 && p_76495_1_ != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
    }

    void placeLeaves(BlockPos blockPos)
    {
        for (int i = 0; i < this.leafDistanceLimit; ++i)
        {
            this.createLeaves(blockPos.above(i), this.leafSize(i), trunk.defaultBlockState());
        }
    }

    void placeBlock(BlockPos blockPos, BlockPos blockPos1, Block block)
    {
        BlockPos blockPos2 = blockPos1.offset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
        int i = this.getHighestAbsoluteCoordinate(blockPos2);
        float f = (float) blockPos2.getX() / (float) i;
        float f1 = (float) blockPos2.getY() / (float) i;
        float f2 = (float) blockPos2.getZ() / (float) i;

        for (int j = 0; j <= i; ++j)
        {
            BlockPos blockPos3 = blockPos.offset(BlockPos.containing(0.5F + (float) j * f, 0.5F + (float) j * f1, 0.5F + (float) j * f2));
            this.setBlock(this.level, blockPos3, block.defaultBlockState());
        }
    }

    private int getHighestAbsoluteCoordinate(BlockPos p_175935_1_)
    {
        int i = Mth.abs(p_175935_1_.getX());
        int j = Mth.abs(p_175935_1_.getY());
        int k = Mth.abs(p_175935_1_.getZ());
        return k > i && k > j ? k : (Math.max(j, i));
    }

    void createLeafNodes()
    {

        for (FoliageCoordinates foliagecoordinates : this.foliageCoords) {
            this.placeLeaves(foliagecoordinates);
        }
    }

    /**
     * Indicates whether or not a leaf node requires additional wood to be added to preserve integrity.
     */
    boolean leafNodeNeedsBase(int p_76493_1_)
    {
        return (double) p_76493_1_ >= (double) this.heightLimit * 0.2D;
    }

    void buildTrunk()
    {
        BlockPos blockpos = this.origin;
        BlockPos blockpos1 = this.origin.above(this.height);
        Block block = trunk;
        this.placeBlock(blockpos, blockpos1, block);

        if (this.spread == 2)
        {
            this.placeBlock(blockpos.east(), blockpos1.east(), block);
            this.placeBlock(blockpos.east().south(), blockpos1.east().south(), block);
            this.placeBlock(blockpos.south(), blockpos1.south(), block);
        }
    }

    void placeLeaves()
    {

        for (FoliageCoordinates foliageCoordinates : this.foliageCoords) {
            int i = foliageCoordinates.getHeight();
            BlockPos blockpos = new BlockPos(this.origin.getX(), i, this.origin.getZ());

            if (this.leafNodeNeedsBase(i - this.origin.getY())) {
                this.placeBlock(blockpos, foliageCoordinates, trunk);
            }
        }
    }

    int func_175936_a(BlockPos blockPos, BlockPos blockPos1)
    {
        BlockPos blockPos2 = blockPos1.offset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
        int i = this.getHighestAbsoluteCoordinate(blockPos2);
        float f = (float) blockPos2.getX() / (float) i;
        float f1 = (float) blockPos2.getY() / (float) i;
        float f2 = (float) blockPos2.getZ() / (float) i;

        if (i != 0) {
            for (int j = 0; j <= i; ++j) {
                blockPos.offset(BlockPos.containing(0.5F + (float) j * f, 0.5F + (float) j * f1, 0.5F + (float) j * f2));
            }

        }
        return -1;
    }

    /**
     * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
     * limit, is valid.
     */
    private boolean validTreeLocation()
    {
        BlockPos down = this.origin.below();
        BlockState state = this.level.getBlockState(down);
        boolean isSoil = state.getBlock() == soil;

        if (!isSoil)
        {
            return false;
        }
        else
        {
            int i = this.func_175936_a(this.origin, this.origin.above(this.heightLimit - 1));

            if (i == -1)
            {
                return true;
            }
            else if (i < 6)
            {
                return false;
            }
            else
            {
                this.heightLimit = i;
                return true;
            }
        }
    }

    @Override
    public void setMetas(int[] newMetas) {
        metas = newMetas;
    }

    @Override
    public void setSoil(Block newSoil) {
        soil = newSoil;
    }

    @Override
    public boolean place(Level worldIn, RandomSource rand, BlockPos position) {
        this.level = worldIn;
        this.origin = position;
        this.random = new Random(rand.nextLong());
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        blocks.addAll(PandorasBox.wool);
        trunk = blocks.get(metas[rand.nextInt(metas.length)]);

        if(level == null) return false;
        if (this.heightLimit == 0)
        {
            this.heightLimit = 5 + this.random.nextInt(this.maxHeight);
        }

        if (!this.validTreeLocation())
        {
            this.level = null; //Fix vanilla Mem leak, holds latest world
            return false;
        }
        else
        {
            this.generateLeafNodeList();
            this.createLeafNodes();
            this.buildTrunk();
            this.placeLeaves();
            this.level = null; //Fix vanilla Mem leak, holds latest world
            return true;
        }
    }

    static class FoliageCoordinates extends BlockPos
    {
        private final int height;

        public FoliageCoordinates(BlockPos blockPos, int height)
        {
            super(blockPos);
            this.height = height;
        }

        public int getHeight()
        {
            return this.height;
        }
    }
}