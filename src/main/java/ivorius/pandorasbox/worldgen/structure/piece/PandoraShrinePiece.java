package ivorius.pandorasbox.worldgen.structure.piece;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import ivorius.pandorasbox.init.StructureInit;
import ivorius.pandorasbox.worldgen.structure.processor.EndStoneReplaceProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.slf4j.Logger;

public class PandoraShrinePiece extends TemplateStructurePiece {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Properties properties;

	public PandoraShrinePiece(
		StructureTemplateManager manager,
		BlockPos pos,
		Properties properties,
		ResourceLocation location,
		Rotation rotation,
		Mirror mirror,
		BlockPos pivot
	) {
		super(StructureInit.PANDORA_SHRINE_PIECE, 0, manager, location, location.toString(), makeSettings(mirror, rotation, pivot, properties), pos);
		this.properties = properties;
	}

	public PandoraShrinePiece(StructureTemplateManager manager, CompoundTag tag) {
		super(StructureInit.PANDORA_SHRINE_PIECE, tag, manager, arg3 -> makeSettings(manager, tag, arg3));
		this.properties = Properties.CODEC.parse(NbtOps.INSTANCE, tag.get("properties")).getOrThrow();
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
		super.addAdditionalSaveData(ctx, tag);
		tag.putString("Rotation", this.placeSettings.getRotation().name());
		tag.putString("Mirror", this.placeSettings.getMirror().name());
		Properties.CODEC.encodeStart(NbtOps.INSTANCE, this.properties)
			.resultOrPartial(LOGGER::error)
			.ifPresent(properties -> tag.put("properties", properties));
	}

	private static StructurePlaceSettings makeSettings(StructureTemplateManager manager, CompoundTag tag, ResourceLocation resourceLocation) {
		StructureTemplate structureTemplate = manager.getOrCreate(resourceLocation);
		BlockPos blockPos = new BlockPos(structureTemplate.getSize().getX() / 2, 0, structureTemplate.getSize().getZ() / 2);
		return makeSettings(Mirror.valueOf(tag.getString("Mirror")),
                Rotation.valueOf(tag.getString("Rotation")),
                blockPos,
                Properties.CODEC.parse(NbtOps.INSTANCE, tag.get("properties")).getOrThrow());
	}

	private static StructurePlaceSettings makeSettings(Mirror mirror, Rotation rotation, BlockPos pivot, Properties properties) {
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(mirror)
                .setRotationPivot(pivot)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE));
        if (properties.replaceWithEndStone) settings.addProcessor(EndStoneReplaceProcessor.INSTANCE);
        return settings;
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos blockPos) {
		BoundingBox boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
		if (box.isInside(boundingBox.getCenter())) {
			box.encapsulate(boundingBox);
			super.postProcess(level, manager, generator, random, box, chunkPos, blockPos);
			this.spreadGrowth(random, level);
			this.addMudBelowShrine(random, level);
            BlockPos.betweenClosedStream(this.getBoundingBox()).forEach(pos -> {
                if (this.properties.overgrown) this.maybeAddVines(random, level, pos);
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof PandorasBoxBlockEntity pandorasBoxBlockEntity) {
                    float rotationYaw = pandorasBoxBlockEntity.getRotationYaw() + 180F;
                    pandorasBoxBlockEntity.setRotationYaw(switch (getRotation()) {
                        case NONE -> rotationYaw;
                        case CLOCKWISE_90 -> rotationYaw + 90F;
                        case CLOCKWISE_180 -> rotationYaw + 180;
                        case COUNTERCLOCKWISE_90 -> rotationYaw - 90F;
                    });
                }
            });
		}
	}

	@Override
	protected void handleDataMarker(String string, BlockPos arg, ServerLevelAccessor arg2, RandomSource arg3, BoundingBox arg4) {
	}

    private void maybeAddVines(RandomSource random, LevelAccessor level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (!blockState.isAir() && !blockState.is(Blocks.VINE)) {
            Direction direction = getRandomHorizontalDirection(random);
            BlockPos supportingDir = pos.relative(direction);
            BlockState state = level.getBlockState(supportingDir);
            if (state.isAir()) {
                if (Block.isFaceFull(blockState.getCollisionShape(level, pos), direction)) {
                    BooleanProperty booleanProperty = VineBlock.getPropertyForFace(direction.getOpposite());
                    level.setBlock(supportingDir, Blocks.VINE.defaultBlockState().setValue(booleanProperty, true), 3);
                }
            }
        }
    }

    private void maybeAddMossCarpetAbove(RandomSource random, LevelAccessor level, BlockPos pos) {
        if (random.nextFloat() < 0.5F && level.getBlockState(pos.above()).isAir()) {
            level.setBlock(pos.above(), Blocks.MOSS_CARPET.defaultBlockState(), 3);
        }
    }

	private void addMudBelowShrine(RandomSource random, LevelAccessor level) {
        BlockPos centerPos = this.boundingBox.getCenter();
        double maxDist = centerPos.distToCenterSqr(this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ());
		for (int x = this.boundingBox.minX(); x <= this.boundingBox.maxX(); x++) {
			for (int z = this.boundingBox.minZ(); z <= this.boundingBox.maxZ(); z++) {
				BlockPos pos = new BlockPos(x, this.boundingBox.minY(), z);
                this.fillWithMud(random, level, pos.below(), centerPos, maxDist);
			}
		}
	}

	private void fillWithMud(RandomSource random, LevelAccessor level, BlockPos pos, BlockPos centerPos, double maxDist) {
		BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
		this.placeMud(level, mutableBlockPos);
		int position = 8;
        double baseDistance = centerPos.distSqr(mutableBlockPos) / 2;
        double distance = 0;

		while (position > -16 && (random.nextFloat() < 0.5F || distance < maxDist)) {
			mutableBlockPos.move(Direction.DOWN);
			position--;
			this.placeMud(level, mutableBlockPos);
            distance = centerPos.distSqr(mutableBlockPos) - baseDistance;
		}
	}

	private void spreadGrowth(RandomSource random, LevelAccessor level) {
		BlockPos center = this.boundingBox.getCenter();
        double maxDist = center.distToCenterSqr(this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ());
		int centerX = center.getX();
		int centerZ = center.getZ();
		float[] chances = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.75F, 0.6F, 0.5F, 0.5F};
		int chanceCount = chances.length;
		int totalDist = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
		int offset = random.nextInt(Math.max(1, 4 - totalDist / 2));
		BlockPos.MutableBlockPos mutableBlockPos = BlockPos.ZERO.mutable();

		for (int x = centerX - chanceCount; x <= centerX + chanceCount; x++) {
			for (int z = centerZ - chanceCount; z <= centerZ + chanceCount; z++) {
				int distance = Math.abs(x - centerX) + Math.abs(z - centerZ);
				int chanceIndex = Math.max(0, distance + offset);
				if (chanceIndex < chanceCount) {
					float chance = chances[chanceIndex];
					if (random.nextDouble() < chance) {
						int surfaceY = getSurfaceY(level, x, z);
                        mutableBlockPos.set(x, surfaceY, z);
						if (Math.abs(surfaceY - this.boundingBox.minY()) <= 3 && this.canBlockBeReplacedByMud(level, mutableBlockPos) && !this.boundingBox.isInside(mutableBlockPos)) {
							this.placeMudOrMoss(random, level, mutableBlockPos);
                            this.maybeAddMossCarpetAbove(random, level, mutableBlockPos);

							this.fillWithMud(random, level, mutableBlockPos.below(), center, maxDist);
						}
					}
				}
			}
		}
	}

	private boolean canBlockBeReplacedByMud(LevelAccessor level, BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		return !blockState.is(Blocks.AIR)
			&& !blockState.is(Blocks.OBSIDIAN)
			&& !blockState.is(BlockTags.FEATURES_CANNOT_REPLACE);
	}

	private void placeMud(LevelAccessor level, BlockPos pos) {
        level.setBlock(pos, Blocks.MUD.defaultBlockState(), 3);
	}

    private void placeMudOrMoss(RandomSource random, LevelAccessor level, BlockPos pos) {
        if (random.nextFloat() > 0.3) level.setBlock(pos, Blocks.MUD.defaultBlockState(), 3);
        else level.setBlock(pos, Blocks.MOSS_BLOCK.defaultBlockState(), 3);
    }

	private static int getSurfaceY(LevelAccessor level, int x, int z) {
		return level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
	}

    public static class Properties {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codec.BOOL.fieldOf("replace_with_end_stone").forGetter(arg -> arg.replaceWithEndStone),
                                Codec.BOOL.fieldOf("overgrown").forGetter(arg -> arg.overgrown)
                        )
                        .apply(instance, Properties::new)
        );
        public boolean replaceWithEndStone;
        public boolean overgrown;

        public Properties(boolean replaceWithEndStone, boolean overgrown) {
            this.replaceWithEndStone = replaceWithEndStone;
            this.overgrown = overgrown;
        }
    }
}