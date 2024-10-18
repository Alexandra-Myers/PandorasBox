/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.worldgen;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBox;
import net.atlas.atlascore.util.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class WorldGenLollipop extends TreeFeature implements AccessibleTreeFeature {
    public static int[] metas;
    public static Block soil;
    public final int addition;

    public WorldGenLollipop(Codec<TreeConfiguration> configIn, int addition) {
        super(configIn);
        this.addition = addition;
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
    public boolean place(Level world, RandomSource rand, BlockPos position) {
        int l = rand.nextInt(addition) + 5;
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        blocks.addAll(PandorasBox.wool);

        boolean flag = true;

        int posX = position.getX();
        int posY = position.getY();
        int posZ = position.getZ();

        if(world == null) return false;
        if (posY >= -63 && posY + l + 1 <= 320) {
            for (int i = posY; i <= posY + 1 + l; ++i) {
                flag &= i >= -64 && i < 320;
            }

            if (!flag) {
                return false;
            } else {
                BlockPos pos = position.below();
                BlockState block2State = world.getBlockState(pos);
                Block block2 = block2State.getBlock();
                boolean rotated = rand.nextBoolean();

                boolean isSoil = block2 == soil;
                if (isSoil && posY < 320 - l - 1) {
                    int k2;

                    for (int shift = -1; shift <= 1; shift++) {
                        for (int s = -l / 2; s <= l / 2; s++) {
                            for (int y = -l / 2; y <= l / 2; y++) {
                                if (s * s + y * y <= (l * l / 4 - shift * shift * 4)) {
                                    int x = (!rotated ? s : shift) + posX;
                                    int z = (rotated ? s : shift) + posZ;
                                    int rY = y + posY + l;

                                    BlockPos pos1 = new BlockPos(x, rY, z);
                                    BlockState block1State = world.getBlockState(pos1);

                                    if (block1State.isAir() || block1State.is(BlockTags.LEAVES)) {
                                        Block block = blocks.get(metas[rand.nextInt(metas.length)]);
                                        this.setBlock(world, pos1, block.defaultBlockState());
                                    }
                                }
                            }
                        }
                    }

                    for (k2 = 0; k2 < l; ++k2) {
                        BlockPos pos1 = new BlockPos(posX, posY + k2, posZ);
                        BlockState block3State = world.getBlockState(pos1);

                        if (block3State.isAir() || block3State.is(BlockTags.LEAVES)) {
                            Block block = blocks.get(metas[0]);
                            this.setBlock(world, pos1, block.defaultBlockState());
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}