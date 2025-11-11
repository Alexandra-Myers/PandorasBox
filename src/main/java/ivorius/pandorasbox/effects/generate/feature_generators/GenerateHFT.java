package ivorius.pandorasbox.effects.generate.feature_generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.FeatureInit;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.worldgen.RestrictedColorFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record GenerateHFT(TagKey<Block> blocks, Integer[] groundMetas, IValue colorVariantCount) implements FeatureGenerator {
    public static final MapCodec<GenerateHFT> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(TagKey.codec(Registries.BLOCK).fieldOf("blocks").forGetter(GenerateHFT::blocks),
                            PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("ground_metas").forGetter(GenerateHFT::groundMetas),
                            IValue.CODEC.fieldOf("color_variant_count").forGetter(GenerateHFT::colorVariantCount))
                    .apply(instance, GenerateHFT::new));
    @Override
    public void finalGenerate(ServerLevel serverLevel, BlockPos pos, BlockState blockState, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        HolderSet.Named<Block> terracotta = BuiltInRegistries.BLOCK.getOrThrow(blocks);
        Block placeBlock = terracotta.get(groundMetas[random.nextInt(groundMetas.length)] % terracotta.size()).value();
        if (random.nextInt(512) == 0) {
            BlockPos down = pos.below();
            BlockState state = serverLevel.getBlockState(down);
            boolean isSoil = state.is(blocks);
            if (!isSoil) return;

            Optional<Registry<ConfiguredFeature<?, ?>>> registry = serverLevel.registryAccess().lookup(Registries.CONFIGURED_FEATURE);
            ConfiguredFeature<?, ?> tree;

            if (random.nextFloat() > 0.5) {
                if (registry.isEmpty()) return;
                tree = registry.get().getValueOrThrow(FeatureInit.LOLLIPOPS);
            } else if (random.nextFloat() > 0.4) {
                if (registry.isEmpty()) return;
                tree = registry.get().getValueOrThrow(TreeFeatures.FANCY_OAK);
            } else {
                if (registry.isEmpty()) return;
                tree = registry.get().getValueOrThrow(FeatureInit.RAINBOWS);
            }
            RestrictedColorFeature.placeWithRestrictedColors(tree, serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos, colorVariantCount, placeBlock.defaultBlockState());
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
