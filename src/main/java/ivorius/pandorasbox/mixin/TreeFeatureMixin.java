package ivorius.pandorasbox.mixin;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.extension.TreeFeatureExtensions;
import ivorius.pandorasbox.init.PandoraStructureTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

    @Inject(method = "place", at = @At(value = "HEAD"), cancellable = true)
    public void disablePlacementAroundHenge(FeaturePlaceContext<TreeConfiguration> arg, CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos = arg.origin();
        SectionPos sectionPos = SectionPos.of(pos);
        ChunkAccess chunkAccess = arg.level().getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_REFERENCES);
        Optional<Registry<Structure>> structures = arg.level().registryAccess().registry(Registries.STRUCTURE);
        if (structures.isPresent() && !chunkAccess.getStatus().isOrAfter(ChunkStatus.FULL)) {
            AtomicBoolean fail = new AtomicBoolean(false);
            chunkAccess.getAllStarts().forEach((structure, structureStart) -> {
                if (structureStart.getBoundingBox().inflatedBy(128).isInside(pos)) fail.set(true);
            });
            if (fail.get()) {
                cir.setReturnValue(false);
                return;
            }
            int minX = sectionPos.x() - 1;
            int maxX = sectionPos.x() + 1;
            int minZ = sectionPos.z() - 1;
            int maxZ = sectionPos.z() + 1;
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (sectionPos.x() == x && sectionPos.z() == z) continue;
                    chunkAccess = arg.level().getChunk(x, z, ChunkStatus.STRUCTURE_REFERENCES);
                    if (!chunkAccess.hasAnyStructureReferences()) continue;
                    chunkAccess.getAllStarts().forEach((structure, structureStart) -> {
                        if (structures.get().wrapAsHolder(structure).is(PandoraStructureTags.BLOCKS_NEARBY_TREES) && structureStart.getBoundingBox().inflatedBy(128).isInside(pos)) fail.set(true);
                    });
                    if (fail.get()) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }

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

            return BoundingBox.encapsulatingPositions(Iterables.concat(set, set2, set3, set4)).map(boundingBox -> {
                DiscreteVoxelShape discreteVoxelShape = updateLeaves(worldGenLevel, boundingBox, set2, set4, set);
                StructureTemplate.updateShapeAtEdge(worldGenLevel, 3, discreteVoxelShape, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }
}
