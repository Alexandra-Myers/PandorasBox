package ivorius.pandorasbox.mixin;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.extension.TreeFeatureExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin extends Feature<@NotNull TreeConfiguration> implements TreeFeatureExtensions<TreeConfiguration> {
    public TreeFeatureMixin(Codec<TreeConfiguration> codec) {
        super(codec);
    }

    @Shadow
    private static DiscreteVoxelShape updateLeaves(LevelAccessor levelAccessor, BoundingBox boundingBox, Set<BlockPos> set, Set<BlockPos> set2, Set<BlockPos> set3) {
        return null;
    }

    @Shadow
    protected abstract boolean doPlace(WorldGenLevel worldGenLevel, RandomSource randomSource, BlockPos blockPos, BiConsumer<BlockPos, BlockState> biConsumer, BiConsumer<BlockPos, BlockState> biConsumer2, FoliagePlacer.FoliageSetter foliageSetter, TreeConfiguration treeConfiguration);

    @Override
    public boolean pandorasBox$placeWithBlockOverrides(FeaturePlaceContext<TreeConfiguration> featurePlaceContext, BlockState trunk, BlockState leaves, @Nullable BlockState soil) {
        final WorldGenLevel worldGenLevel = featurePlaceContext.level();
        RandomSource randomSource = featurePlaceContext.random();
        BlockPos blockPos = featurePlaceContext.origin();
        TreeConfiguration treeConfiguration = featurePlaceContext.config();
        Set<BlockPos> set = Sets.<BlockPos>newHashSet();
        Set<BlockPos> set2 = Sets.<BlockPos>newHashSet();
        final Set<BlockPos> set3 = Sets.<BlockPos>newHashSet();
        Set<BlockPos> set4 = Sets.<BlockPos>newHashSet();
        BiConsumer<BlockPos, BlockState> biConsumer = (blockPosx, blockState) -> {
            set.add(blockPosx.immutable());
            worldGenLevel.setBlock(blockPosx, blockState, 19);
        };
        BlockPos trunkPos = treeConfiguration.rootPlacer.map(rootPlacer -> rootPlacer.getTrunkOrigin(blockPos, randomSource)).orElse(blockPos);
        Optional<BlockState> soilOverride = Optional.ofNullable(soil);
        BiConsumer<BlockPos, BlockState> biConsumer2 = (blockPosx, blockState) -> {
            set2.add(blockPosx.immutable());
            boolean isDirt = blockPosx.getY() <= trunkPos.below().getY();
            worldGenLevel.setBlock(blockPosx, isDirt ? soilOverride.orElse(blockState) : trunk, 19);
        };
        FoliagePlacer.FoliageSetter foliageSetter = new FoliagePlacer.FoliageSetter() {
            @Override
            public void set(BlockPos blockPos, BlockState blockState) {
                set3.add(blockPos.immutable());
                worldGenLevel.setBlock(blockPos, leaves, 19);
            }

            @Override
            public boolean isSet(BlockPos blockPos) {
                return set3.contains(blockPos);
            }
        };
        BiConsumer<BlockPos, BlockState> biConsumer3 = (blockPosx, blockState) -> {
            set4.add(blockPosx.immutable());
            worldGenLevel.setBlock(blockPosx, blockState, 19);
        };
        boolean bl = this.doPlace(worldGenLevel, randomSource, blockPos, biConsumer, biConsumer2, foliageSetter, treeConfiguration);
        if (bl && (!set2.isEmpty() || !set3.isEmpty())) {
            if (!treeConfiguration.decorators.isEmpty()) {
                TreeDecorator.Context context = new TreeDecorator.Context(worldGenLevel, biConsumer3, randomSource, set2, set3, set);
                treeConfiguration.decorators.forEach(treeDecorator -> treeDecorator.place(context));
            }

            return (Boolean) BoundingBox.encapsulatingPositions(Iterables.concat(set, set2, set3, set4)).map(boundingBox -> {
                DiscreteVoxelShape discreteVoxelShape = updateLeaves(worldGenLevel, boundingBox, set2, set4, set);
                StructureTemplate.updateShapeAtEdge(worldGenLevel, 3, discreteVoxelShape, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }
}
