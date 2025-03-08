package ivorius.pandorasbox.effects.generate.feature_generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.FeatureInit;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.worldgen.AccessibleTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record GenerateHFT(Integer[] groundMetas) implements FeatureGenerator {
    public static final MapCodec<GenerateHFT> CODEC = PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("ground_metas").xmap(GenerateHFT::new, GenerateHFT::groundMetas);
    @Override
    public void finalGenerate(ServerLevel serverLevel, BlockPos pos, BlockState blockState, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        HolderSet.Named<Block> terracotta = BuiltInRegistries.BLOCK.getOrThrow(PandorasBox.ALL_TERRACOTTA);
        Block placeBlock = terracotta.get(groundMetas[random.nextInt(groundMetas.length)] % terracotta.size()).value();
        if (random.nextInt(10 * 10) == 0) {
            int[] lolliColors = new int[random.nextInt(4) + 1];
            for (int i = 0; i < lolliColors.length; i++) {
                lolliColors[i] = random.nextInt(16);
            }

            AccessibleTreeFeature treeFeature = (AccessibleTreeFeature) FeatureInit.RAINBOW;

            if (random.nextFloat() > 0.5) {
                treeFeature = (AccessibleTreeFeature) FeatureInit.LOLIPOP;
            } else if (random.nextFloat() > 0.4) {
                treeFeature = (AccessibleTreeFeature) FeatureInit.COLOURFUL_TREE;
            }
            treeFeature.setMetas(lolliColors);
            treeFeature.setSoil(placeBlock);
            treeFeature.place(serverLevel, random, pos);
        } else if (random.nextInt(5 * 5) == 0) {
            if (serverLevel.getBlockState(pos.below()).getBlock() == placeBlock && serverLevel.getBlockState(pos).isAir()) {
                if (random.nextBoolean()) {
                    setBlockSafe(serverLevel, pos, Blocks.CAKE.defaultBlockState());
                } else {
                    ItemEntity entityItem = new ItemEntity(serverLevel, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                    entityItem.setPickUpDelay(20);
                    serverLevel.addFreshEntity(entityItem);
                }
            }
        }
    }

    @Override
    public double baseChance() {
        return 1;
    }

    @Override
    public @NotNull MapCodec<? extends FeatureGenerator> codec() {
        return CODEC;
    }
}
