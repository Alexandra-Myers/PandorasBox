package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectWorldGenStructure;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.weighted.WeightedStructure;
import ivorius.pandorasbox.worldgen.NosyWorldGenLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public record PBECStructure(float chanceForMoreEffects, IValue blocksPerTick, List<WeightedStructure> structures) implements PBEffectCreator {
    public static final MapCodec<PBECStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.floatRange(0, 1).fieldOf("chance_for_more_effects").forGetter(PBECStructure::chanceForMoreEffects),
                            IValue.CODEC.fieldOf("blocks_per_tick").forGetter(PBECStructure::blocksPerTick),
                            WeightedStructure.CODEC.listOf().fieldOf("structures").forGetter(PBECStructure::structures))
                    .apply(instance, PBECStructure::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int blocksPerTick = Math.max(1, this.blocksPerTick.getValue(random));
        ConcurrentLinkedDeque<Runnable> reset = new ConcurrentLinkedDeque<>();
        List<BlockUpdateData> data = new ArrayList<>();
        List<List<BlockState>> palettes = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        if (world instanceof ServerLevel serverLevel) {
            NosyWorldGenLevel nosyWorldGenLevel = new NosyWorldGenLevel(data, palettes, reset, entities, serverLevel);
            BlockPos pos = BlockPos.containing(x, y, z);
            ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
            List<WeightedStructure> tempStructures = new ArrayList<>(structures);

            while (!tempStructures.isEmpty()) {
                WeightedStructure structure = WeightedSelector.selectItem(random, tempStructures);

                StructureStart structureStart = structure.structure().get().generate(world.registryAccess(),
                        generator,
                        generator.getBiomeSource(),
                        serverLevel.getChunkSource().randomState(),
                        serverLevel.getStructureManager(),
                        serverLevel.getSeed(),
                        new ChunkPos(pos),
                        0,
                        serverLevel,
                        argx -> true);
                if (structureStart.isValid()) {
                    BoundingBox boundingBox = structureStart.getBoundingBox();
                    ChunkPos minChunk = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
                    ChunkPos maxChunk = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));
                    if (areChunksLoaded(serverLevel, minChunk, maxChunk)) {
                        ChunkPos.rangeClosed(minChunk, maxChunk)
                                .forEach(
                                        inChunk -> structureStart.placeInChunk(
                                                nosyWorldGenLevel,
                                                serverLevel.structureManager(),
                                                generator,
                                                random,
                                                new BoundingBox(inChunk.getMinBlockX(), serverLevel.getMinBuildHeight(), inChunk.getMinBlockZ(), inChunk.getMaxBlockX(), serverLevel.getMaxBuildHeight(), inChunk.getMaxBlockZ()),
                                                inChunk
                                        )
                                );
                        break;
                    }
                }
                tempStructures.remove(structure);
            }
        }
        while (!reset.isEmpty()) {
            Runnable toReset = reset.poll();
            if (toReset != null) toReset.run();
        }
        return new PBEffectWorldGenStructure(data.size() / blocksPerTick + 20, blocksPerTick, palettes, data, Collections.emptyList(), entities);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return chanceForMoreEffects;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }

    public static boolean areChunksLoaded(ServerLevel level, ChunkPos minChunk, ChunkPos maxChunk) {
        return ChunkPos.rangeClosed(minChunk, maxChunk).allMatch(chunk -> level.isLoaded(chunk.getWorldPosition()));
    }

    public static final class BlockUpdateData {
        public static final Codec<BlockUpdateData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(BlockUpdateData::pos),
                                Codec.INT.fieldOf("palette").forGetter(BlockUpdateData::palette),
                                Codec.INT.fieldOf("state").forGetter(BlockUpdateData::id),
                                CompoundTag.CODEC.optionalFieldOf("tag").forGetter(BlockUpdateData::tag))
                        .apply(instance, BlockUpdateData::new));
        private final BlockPos pos;
        private final int palette;
        private final int id;
        private BlockState state;
        private Optional<CompoundTag> tag;

        public BlockUpdateData(BlockPos pos, BlockState state, int palette, int id, Optional<CompoundTag> tag) {
            this.pos = pos;
            this.state = state;
            this.palette = palette;
            this.id = id;
            this.tag = tag;
        }

        public BlockUpdateData(BlockPos pos, int palette, int id, Optional<CompoundTag> tag) {
            this.pos = pos;
            this.state = Blocks.AIR.defaultBlockState();
            this.palette = palette;
            this.id = id;
            this.tag = tag;
        }

        public BlockPos pos() {
            return pos;
        }

        public BlockState state() {
            return state;
        }

        public void setState(BlockState state) {
            this.state = state;
        }

        public int palette() {
            return palette;
        }

        public int id() {
            return id;
        }

        public Optional<CompoundTag> tag() {
            return tag;
        }

        public void setTag(CompoundTag tag) {
            this.tag = Optional.of(tag);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BlockUpdateData data)) return false;
            return palette == data.palette && id == data.id && Objects.equals(pos, data.pos) && Objects.equals(tag, data.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, palette, id, tag);
        }

        @Override
        public String toString() {
            return "BlockUpdateData{" +
                    "pos=" + pos +
                    ", palette=" + palette +
                    ", id=" + id +
                    ", tag=" + tag +
                    '}';
        }
    }
}
