package ivorius.pandorasbox.worldgen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.init.StructureInit;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.weighted.WeightedResourceLocation;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.worldgen.structure.piece.PandoraShrinePiece;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;
import java.util.Optional;

public class PandoraShrineStructure extends Structure {
    private final List<WeightedResourceLocation> templates;
    private final ZValue overgrown;
    private final boolean replaceWithEndStone;
    private final Optional<Integer> aboveHeight;
    public static final MapCodec<PandoraShrineStructure> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(settingsCodec(instance),
                            WeightedResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(arg -> arg.templates),
                            ZValue.CODEC.fieldOf("overgrown").forGetter(arg -> arg.overgrown),
                            Codec.BOOL.fieldOf("replace_with_end_stone").forGetter(arg -> arg.replaceWithEndStone),
                            Codec.INT.optionalFieldOf("above_height").forGetter(arg -> arg.aboveHeight))
                    .apply(instance, PandoraShrineStructure::new));

    public PandoraShrineStructure(Structure.StructureSettings arg, List<WeightedResourceLocation> templates, ZValue overgrown, boolean replaceWithEndStone, Optional<Integer> aboveHeight) {
        super(arg);
        this.overgrown = overgrown;
        this.templates = templates;
        this.replaceWithEndStone = replaceWithEndStone;
        this.aboveHeight = aboveHeight;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext ctx) {
        WorldgenRandom random = ctx.random();
        ResourceLocation possibleStructures = WeightedSelector.selectItem(random, templates).resourceLocation();

        StructureTemplate structureTemplate = ctx.structureTemplateManager().getOrCreate(possibleStructures);
        Rotation rotation = Util.getRandom(Rotation.values(), random);
        Mirror mirror = random.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
        BlockPos blockPos = new BlockPos(structureTemplate.getSize().getX() / 2, 0, structureTemplate.getSize().getZ() / 2);
        ChunkGenerator chunkGenerator = ctx.chunkGenerator();
        LevelHeightAccessor levelHeightAccessor = ctx.heightAccessor();
        RandomState randomState = ctx.randomState();
        BlockPos blockPos2 = ctx.chunkPos().getWorldPosition();
        BoundingBox boundingBox = structureTemplate.getBoundingBox(blockPos2, rotation, blockPos, mirror);
        BlockPos blockPos3 = boundingBox.getCenter();
        int baseHeight = chunkGenerator.getBaseHeight(blockPos3.getX(), blockPos3.getZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor, randomState) - 1;
        int yPos = findSuitableY(chunkGenerator, baseHeight, boundingBox, levelHeightAccessor, randomState);
        if (aboveHeight.isPresent() && yPos < aboveHeight.get()) return Optional.empty();
        BlockPos atPos = new BlockPos(blockPos2.getX(), yPos, blockPos2.getZ());
        return Optional.of(
                new Structure.GenerationStub(
                        atPos,
                        builder -> builder.addPiece(
                                new PandoraShrinePiece(ctx.structureTemplateManager(), atPos, new PandoraShrinePiece.Properties(replaceWithEndStone, overgrown.getValue(random)), possibleStructures, rotation, mirror, blockPos)
                        )
                )
        );
    }

    private static int findSuitableY(
            ChunkGenerator chunkGenerator,
            int startingHeight,
            BoundingBox boundingBox,
            LevelHeightAccessor levelHeightAccessor,
            RandomState state
    ) {
        int lowestAcceptedHeight = levelHeightAccessor.getMinY() + 15;

        List<BlockPos> positions = ImmutableList.of(
                new BlockPos(boundingBox.minX(), 0, boundingBox.minZ()),
                new BlockPos(boundingBox.maxX(), 0, boundingBox.minZ()),
                new BlockPos(boundingBox.minX(), 0, boundingBox.maxZ()),
                new BlockPos(boundingBox.maxX(), 0, boundingBox.maxZ())
        );
        List<NoiseColumn> noiseColumns = positions.stream()
                .map(arg4x -> chunkGenerator.getBaseColumn(arg4x.getX(), arg4x.getZ(), levelHeightAccessor, state))
                .toList();
        Heightmap.Types types = Heightmap.Types.WORLD_SURFACE_WG;

        int finalY;
        for (finalY = startingHeight; finalY > lowestAcceptedHeight; finalY--) {
            int overrideCnt = 0;

            for (NoiseColumn noiseColumn : noiseColumns) {
                BlockState blockState = noiseColumn.getBlock(finalY);
                if (types.isOpaque().test(blockState)) {
                    if (++overrideCnt == 3) {
                        return finalY;
                    }
                }
            }
        }

        return finalY;
    }

    @Override
    public StructureType<?> type() {
        return StructureInit.PANDORA_SHRINE;
    }
}