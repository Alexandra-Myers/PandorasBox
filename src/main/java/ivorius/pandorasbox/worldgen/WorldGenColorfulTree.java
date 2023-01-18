/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.worldgen;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenColorfulTree extends TreeFeature implements AccessibleTreeFeature
{
    public Block trunk;
    public int[] metas;
    public Block soil;

    private Random field_175949_k;
    private World field_175946_l;
    private BlockPos field_175947_m;
    int heightLimit;
    int height;
    double heightAttenuation;
    double field_175944_d;
    double field_175945_e;
    double leafDensity;
    int field_175943_g;
    int field_175950_h;
    /**
     * Sets the distance limit for how far away the generator will populate leaves from the base leaf node.
     */
    int leafDistanceLimit;
    List field_175948_j;

    public WorldGenColorfulTree(Codec<BaseTreeFeatureConfig> configIn, int height)
    {
        super(configIn);
        this.field_175947_m = BlockPos.ZERO;
        this.heightAttenuation = 0.618D;
        this.field_175944_d = 0.381D;
        this.field_175945_e = 1.0D;
        this.leafDensity = 1.0D;
        this.field_175943_g = 1;
        this.field_175950_h = height;
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

        int j = this.field_175947_m.getY() + this.height;
        int k = this.heightLimit - this.leafDistanceLimit;
        this.field_175948_j = Lists.newArrayList();
        this.field_175948_j.add(new FoliageCoordinates(this.field_175947_m.above(k), j));

        for (; k >= 0; --k)
        {
            float f = this.layerSize(k);

            if (f >= 0.0F)
            {
                for (int l = 0; l < i; ++l)
                {
                    double d0 = this.field_175945_e * (double) f * ((double) this.field_175949_k.nextFloat() + 0.328D);
                    double d1 = (double) (this.field_175949_k.nextFloat() * 2.0F) * Math.PI;
                    double d2 = d0 * Math.sin(d1) + 0.5D;
                    double d3 = d0 * Math.cos(d1) + 0.5D;
                    BlockPos blockpos = this.field_175947_m.offset(d2, (double) (k - 1), d3);
                    BlockPos blockpos1 = blockpos.above(this.leafDistanceLimit);

                    if (this.func_175936_a(blockpos, blockpos1) == -1)
                    {
                        int i1 = this.field_175947_m.getX() - blockpos.getX();
                        int j1 = this.field_175947_m.getZ() - blockpos.getZ();
                        double d4 = (double) blockpos.getY() - Math.sqrt((double) (i1 * i1 + j1 * j1)) * this.field_175944_d;
                        int k1 = d4 > (double) j ? j : (int) d4;
                        BlockPos blockpos2 = new BlockPos(this.field_175947_m.getX(), k1, this.field_175947_m.getZ());

                        if (this.func_175936_a(blockpos2, blockpos) == -1)
                        {
                            this.field_175948_j.add(new FoliageCoordinates(blockpos, blockpos2.getY()));
                        }
                    }
                }
            }
        }
    }

    void func_180712_a(BlockPos p_180712_1_, float p_180712_2_, BlockState p_180712_3_)
    {
        int i = (int) ((double) p_180712_2_ + 0.618D);

        for (int j = -i; j <= i; ++j)
        {
            for (int k = -i; k <= i; ++k)
            {
                if (Math.pow((double) Math.abs(j) + 0.5D, 2.0D) + Math.pow((double) Math.abs(k) + 0.5D, 2.0D) <= (double) (p_180712_2_ * p_180712_2_))
                {
                    BlockPos blockpos1 = p_180712_1_.offset(j, 0, k);
                    BlockState state = this.field_175946_l.getBlockState(blockpos1);

                    if (state.getBlock().isAir(state, this.field_175946_l, blockpos1) || state.getBlock().is(BlockTags.LEAVES))
                    {
                        this.setBlock(this.field_175946_l, blockpos1, p_180712_3_);
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
            float f2 = MathHelper.sqrt(f * f - f1 * f1);

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

    void func_175940_a(BlockPos p_175940_1_)
    {
        for (int i = 0; i < this.leafDistanceLimit; ++i)
        {
            this.func_180712_a(p_175940_1_.above(i), this.leafSize(i), trunk.defaultBlockState());
        }
    }

    void func_175937_a(BlockPos p_175937_1_, BlockPos p_175937_2_, Block p_175937_3_)
    {
        BlockPos blockpos2 = p_175937_2_.offset(-p_175937_1_.getX(), -p_175937_1_.getY(), -p_175937_1_.getZ());
        int i = this.func_175935_b(blockpos2);
        float f = (float) blockpos2.getX() / (float) i;
        float f1 = (float) blockpos2.getY() / (float) i;
        float f2 = (float) blockpos2.getZ() / (float) i;

        for (int j = 0; j <= i; ++j)
        {
            BlockPos blockpos3 = p_175937_1_.offset((double) (0.5F + (float) j * f), (double) (0.5F + (float) j * f1), (double) (0.5F + (float) j * f2));
            this.setBlock(this.field_175946_l, blockpos3, p_175937_3_.defaultBlockState());
        }
    }

    private int func_175935_b(BlockPos p_175935_1_)
    {
        int i = MathHelper.abs(p_175935_1_.getX());
        int j = MathHelper.abs(p_175935_1_.getY());
        int k = MathHelper.abs(p_175935_1_.getZ());
        return k > i && k > j ? k : (j > i ? j : i);
    }

    void func_175941_b()
    {

        for (Object o : this.field_175948_j) {
            FoliageCoordinates foliagecoordinates = (FoliageCoordinates) o;
            this.func_175940_a(foliagecoordinates);
        }
    }

    /**
     * Indicates whether or not a leaf node requires additional wood to be added to preserve integrity.
     */
    boolean leafNodeNeedsBase(int p_76493_1_)
    {
        return (double) p_76493_1_ >= (double) this.heightLimit * 0.2D;
    }

    void func_175942_c()
    {
        BlockPos blockpos = this.field_175947_m;
        BlockPos blockpos1 = this.field_175947_m.above(this.height);
        Block block = trunk;
        this.func_175937_a(blockpos, blockpos1, block);

        if (this.field_175943_g == 2)
        {
            this.func_175937_a(blockpos.east(), blockpos1.east(), block);
            this.func_175937_a(blockpos.east().south(), blockpos1.east().south(), block);
            this.func_175937_a(blockpos.south(), blockpos1.south(), block);
        }
    }

    void func_175939_d()
    {

        for (Object o : this.field_175948_j) {
            FoliageCoordinates foliagecoordinates = (FoliageCoordinates) o;
            int i = foliagecoordinates.func_177999_q();
            BlockPos blockpos = new BlockPos(this.field_175947_m.getX(), i, this.field_175947_m.getZ());

            if (this.leafNodeNeedsBase(i - this.field_175947_m.getY())) {
                this.func_175937_a(blockpos, foliagecoordinates, trunk);
            }
        }
    }

    int func_175936_a(BlockPos p_175936_1_, BlockPos p_175936_2_)
    {
        BlockPos blockpos2 = p_175936_2_.offset(-p_175936_1_.getX(), -p_175936_1_.getY(), -p_175936_1_.getZ());
        int i = this.func_175935_b(blockpos2);
        float f = (float) blockpos2.getX() / (float) i;
        float f1 = (float) blockpos2.getY() / (float) i;
        float f2 = (float) blockpos2.getZ() / (float) i;

        if (i == 0)
        {
            return -1;
        }
        else
        {
            for (int j = 0; j <= i; ++j)
            {
                BlockPos blockpos3 = p_175936_1_.offset((double) (0.5F + (float) j * f), (double) (0.5F + (float) j * f1), (double) (0.5F + (float) j * f2));
            }

            return -1;
        }
    }

    public void func_175904_e()
    {
        this.leafDistanceLimit = 5;
    }

    /**
     * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
     * limit, is valid.
     */
    private boolean validTreeLocation()
    {
        BlockPos down = this.field_175947_m.below();
        BlockState state = this.field_175946_l.getBlockState(down);
        boolean isSoil = state.getBlock() == soil;

        if (!isSoil)
        {
            return false;
        }
        else
        {
            int i = this.func_175936_a(this.field_175947_m, this.field_175947_m.above(this.heightLimit - 1));

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
    public boolean place(IWorldGenerationReader worldIn, Random rand, BlockPos position) {
        this.field_175946_l = worldIn instanceof World ? (World) worldIn : null;
        this.field_175947_m = position;
        this.field_175949_k = new Random(rand.nextLong());
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        for(Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.WOOL.contains(block1)) {
                blocks.add(block1);
            }
        }
        trunk = blocks.get(metas[rand.nextInt(metas.length)]);

        if(field_175946_l == null) return false;
        if (this.heightLimit == 0)
        {
            this.heightLimit = 5 + this.field_175949_k.nextInt(this.field_175950_h);
        }

        if (!this.validTreeLocation())
        {
            this.field_175946_l = null; //Fix vanilla Mem leak, holds latest world
            return false;
        }
        else
        {
            this.generateLeafNodeList();
            this.func_175941_b();
            this.func_175942_c();
            this.func_175939_d();
            this.field_175946_l = null; //Fix vanilla Mem leak, holds latest world
            return true;
        }
    }

    static class FoliageCoordinates extends BlockPos
    {
        private final int field_178000_b;
        private static final String __OBFID = "CL_00002001";

        public FoliageCoordinates(BlockPos p_i45635_1_, int p_i45635_2_)
        {
            super(p_i45635_1_.getX(), p_i45635_1_.getY(), p_i45635_1_.getZ());
            this.field_178000_b = p_i45635_2_;
        }

        public int func_177999_q()
        {
            return this.field_178000_b;
        }
    }
}