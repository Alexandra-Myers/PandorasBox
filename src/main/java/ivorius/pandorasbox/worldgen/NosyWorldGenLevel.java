package ivorius.pandorasbox.worldgen;

import ivorius.pandorasbox.effectcreators.PBECStructure;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public record NosyWorldGenLevel(List<PBECStructure.BlockUpdateData> toEmitTo, List<List<BlockState>> palettes, Deque<Runnable> resetRunners, List<Entity> entities, ServerLevel serverLevel) implements WorldGenLevel {
    @Override
    public long getSeed() {
        return serverLevel.getSeed();
    }

    @Override
    public boolean ensureCanWrite(@NotNull BlockPos arg) {
        return serverLevel.ensureCanWrite(arg);
    }

    @Override
    public void setCurrentlyGenerating(@Nullable Supplier<String> supplier) {
        serverLevel.setCurrentlyGenerating(supplier);
    }

    @Override
    public @NotNull ServerLevel getLevel() {
        return serverLevel.getLevel();
    }

    @Override
    public void addFreshEntityWithPassengers(@NotNull Entity arg) {
        serverLevel.addFreshEntityWithPassengers(arg);
    }

    @Override
    public long dayTime() {
        return serverLevel.dayTime();
    }

    @Override
    public float getMoonBrightness() {
        return serverLevel.getMoonBrightness();
    }

    @Override
    public float getTimeOfDay(float f) {
        return serverLevel.getTimeOfDay(f);
    }

    @Override
    public int getMoonPhase() {
        return serverLevel.getMoonPhase();
    }

    @Override
    public long nextSubTickCount() {
        return serverLevel.nextSubTickCount();
    }

    @Override
    public @NotNull LevelTickAccess<Block> getBlockTicks() {
        return serverLevel.getBlockTicks();
    }

    @Override
    public void scheduleTick(@NotNull BlockPos arg, @NotNull Block arg2, int i, @NotNull TickPriority arg3) {
        serverLevel.scheduleTick(arg, arg2, i, arg3);
    }

    @Override
    public void scheduleTick(@NotNull BlockPos arg, @NotNull Block arg2, int i) {
        serverLevel.scheduleTick(arg, arg2, i);
    }

    @Override
    public @NotNull LevelTickAccess<Fluid> getFluidTicks() {
        return serverLevel.getFluidTicks();
    }

    @Override
    public void scheduleTick(@NotNull BlockPos arg, @NotNull Fluid arg2, int i, @NotNull TickPriority arg3) {
        serverLevel.scheduleTick(arg, arg2, i, arg3);
    }

    @Override
    public void scheduleTick(@NotNull BlockPos arg, @NotNull Fluid arg2, int i) {
        serverLevel.scheduleTick(arg, arg2, i);
    }

    @Override
    public @NotNull LevelData getLevelData() {
        return serverLevel.getLevelData();
    }

    @Override
    public @NotNull DifficultyInstance getCurrentDifficultyAt(@NotNull BlockPos arg) {
        return serverLevel.getCurrentDifficultyAt(arg);
    }

    @Override
    public @NotNull MinecraftServer getServer() {
        return serverLevel.getServer();
    }

    @Override
    public @NotNull Difficulty getDifficulty() {
        return serverLevel.getDifficulty();
    }

    @Override
    public @NotNull ChunkSource getChunkSource() {
        return serverLevel.getChunkSource();
    }

    @Override
    public @Nullable ChunkAccess getChunk(int i, int j, @NotNull ChunkStatus arg, boolean bl) {
        return serverLevel.getChunk(i, j, arg, bl);
    }

    @Override
    public boolean hasChunk(int i, int j) {
        return serverLevel.hasChunk(i, j);
    }

    @Override
    public int getHeight(Heightmap.@NotNull Types arg, int i, int j) {
        return serverLevel.getHeight(arg, i, j);
    }

    @Override
    public int getSkyDarken() {
        return serverLevel.getSkyDarken();
    }

    @Override
    public @NotNull BiomeManager getBiomeManager() {
        return serverLevel.getBiomeManager();
    }

    @Override
    public @NotNull Holder<Biome> getBiome(@NotNull BlockPos arg) {
        return serverLevel.getBiome(arg);
    }

    @Override
    public @NotNull Stream<BlockState> getBlockStatesIfLoaded(@NotNull AABB arg) {
        return serverLevel.getBlockStatesIfLoaded(arg);
    }

    @Override
    public int getBlockTint(@NotNull BlockPos arg, @NotNull ColorResolver arg2) {
        return serverLevel.getBlockTint(arg, arg2);
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int i, int j, int k) {
        return serverLevel.getNoiseBiome(i, j, k);
    }

    @Override
    public @NotNull Holder<Biome> getUncachedNoiseBiome(int i, int j, int k) {
        return serverLevel.getUncachedNoiseBiome(i, j, k);
    }

    @Override
    public boolean isClientSide() {
        return serverLevel.isClientSide();
    }

    @Override
    public int getSeaLevel() {
        return serverLevel.getSeaLevel();
    }

    @Override
    public @NotNull DimensionType dimensionType() {
        return serverLevel.dimensionType();
    }

    @Override
    public int getMinBuildHeight() {
        return serverLevel.getMinBuildHeight();
    }

    @Override
    public int getMaxBuildHeight() {
        return serverLevel.getMaxBuildHeight();
    }

    @Override
    public int getSectionsCount() {
        return serverLevel.getSectionsCount();
    }

    @Override
    public int getMinSection() {
        return serverLevel.getMinSection();
    }

    @Override
    public int getMaxSection() {
        return serverLevel.getMaxSection();
    }

    @Override
    public boolean isOutsideBuildHeight(@NotNull BlockPos arg) {
        return serverLevel.isOutsideBuildHeight(arg);
    }

    @Override
    public boolean isOutsideBuildHeight(int i) {
        return serverLevel.isOutsideBuildHeight(i);
    }

    @Override
    public int getSectionIndex(int i) {
        return serverLevel.getSectionIndex(i);
    }

    @Override
    public int getSectionIndexFromSectionY(int i) {
        return serverLevel.getSectionIndexFromSectionY(i);
    }

    @Override
    public int getSectionYFromSectionIndex(int i) {
        return serverLevel.getSectionYFromSectionIndex(i);
    }

    @Override
    public int getHeight() {
        return serverLevel.getHeight();
    }

    @Override
    public boolean isEmptyBlock(@NotNull BlockPos arg) {
        return serverLevel.isEmptyBlock(arg);
    }

    @Override
    public boolean canSeeSkyFromBelowWater(@NotNull BlockPos arg) {
        return serverLevel.canSeeSkyFromBelowWater(arg);
    }

    @Override
    public float getPathfindingCostFromLightLevels(@NotNull BlockPos arg) {
        return serverLevel.getPathfindingCostFromLightLevels(arg);
    }

    @Override
    public float getLightLevelDependentMagicValue(@NotNull BlockPos arg) {
        return serverLevel.getLightLevelDependentMagicValue(arg);
    }

    @Override
    public @NotNull ChunkAccess getChunk(@NotNull BlockPos arg) {
        return serverLevel.getChunk(arg);
    }

    @Override
    public @NotNull ChunkAccess getChunk(int i, int j) {
        return serverLevel.getChunk(i, j);
    }

    @Override
    public @NotNull ChunkAccess getChunk(int i, int j, @NotNull ChunkStatus arg) {
        return serverLevel.getChunk(i, j, arg);
    }

    @Override
    public @Nullable BlockGetter getChunkForCollisions(int i, int j) {
        return serverLevel.getChunkForCollisions(i, j);
    }

    @Override
    public boolean isWaterAt(@NotNull BlockPos arg) {
        return serverLevel.isWaterAt(arg);
    }

    @Override
    public boolean containsAnyLiquid(@NotNull AABB arg) {
        return serverLevel.containsAnyLiquid(arg);
    }

    @Override
    public int getMaxLocalRawBrightness(@NotNull BlockPos arg) {
        return serverLevel.getMaxLocalRawBrightness(arg);
    }

    @Override
    public int getMaxLocalRawBrightness(@NotNull BlockPos arg, int i) {
        return serverLevel.getMaxLocalRawBrightness(arg, i);
    }

    @Override
    public boolean hasChunkAt(int i, int j) {
        return serverLevel.hasChunkAt(i, j);
    }

    @Override
    public boolean hasChunkAt(@NotNull BlockPos arg) {
        return serverLevel.hasChunkAt(arg);
    }

    @Override
    public boolean isAreaLoaded(@NotNull BlockPos center, int range) {
        return serverLevel.isAreaLoaded(center, range);
    }

    @Override
    public boolean hasChunksAt(@NotNull BlockPos arg, @NotNull BlockPos arg2) {
        return serverLevel.hasChunksAt(arg, arg2);
    }

    @Override
    public boolean hasChunksAt(int i, int j, int k, int l, int m, int n) {
        return serverLevel.hasChunksAt(i, j, k, l, m, n);
    }

    @Override
    public boolean hasChunksAt(int m, int n, int o, int p) {
        return serverLevel.hasChunksAt(m, n, o, p);
    }

    @Override
    public @NotNull RegistryAccess registryAccess() {
        return serverLevel.registryAccess();
    }

    @Override
    public @NotNull FeatureFlagSet enabledFeatures() {
        return serverLevel.enabledFeatures();
    }

    @Override
    public <T> @NotNull HolderLookup<T> holderLookup(@NotNull ResourceKey<? extends Registry<? extends T>> arg) {
        return serverLevel.holderLookup(arg);
    }

    @Override
    public @NotNull RandomSource getRandom() {
        return serverLevel.getRandom();
    }

    @Override
    public void blockUpdated(@NotNull BlockPos arg, @NotNull Block arg2) {
        serverLevel.blockUpdated(arg, arg2);
    }

    @Override
    public void neighborShapeChanged(@NotNull Direction arg, @NotNull BlockState arg2, @NotNull BlockPos arg3, @NotNull BlockPos arg4, int i, int j) {
        serverLevel.neighborShapeChanged(arg, arg2, arg3, arg4, i, j);
    }

    @Override
    public void playSound(@Nullable Player arg, @NotNull BlockPos arg2, @NotNull SoundEvent arg3, @NotNull SoundSource arg4) {
        serverLevel.playSound(arg, arg2, arg3, arg4);
    }

    @Override
    public void playSound(@Nullable Player arg, @NotNull BlockPos arg2, @NotNull SoundEvent arg3, @NotNull SoundSource arg4, float f, float g) {
        serverLevel.playSound(arg, arg2, arg3, arg4, f, g);
    }

    @Override
    public void addParticle(@NotNull ParticleOptions arg, double d, double e, double f, double g, double h, double i) {
        serverLevel.addParticle(arg, d, e, f, g, h, i);
    }

    @Override
    public void levelEvent(@Nullable Player arg, int i, @NotNull BlockPos arg2, int j) {
        serverLevel.levelEvent(arg, i, arg2, j);
    }

    @Override
    public void levelEvent(int i, @NotNull BlockPos arg, int j) {
        serverLevel.levelEvent(i, arg, j);
    }

    @Override
    public void gameEvent(@NotNull GameEvent arg, @NotNull Vec3 arg2, GameEvent.@NotNull Context arg3) {
        serverLevel.gameEvent(arg, arg2, arg3);
    }

    @Override
    public void gameEvent(@Nullable Entity arg, @NotNull GameEvent arg2, @NotNull Vec3 arg3) {
        serverLevel.gameEvent(arg, arg2, arg3);
    }

    @Override
    public void gameEvent(@Nullable Entity arg, @NotNull GameEvent arg2, @NotNull BlockPos arg3) {
        serverLevel.gameEvent(arg, arg2, arg3);
    }

    @Override
    public void gameEvent(@NotNull GameEvent arg, @NotNull BlockPos arg2, GameEvent.@NotNull Context arg3) {
        serverLevel.gameEvent(arg, arg2, arg3);
    }

    @Override
    public float getShade(@NotNull Direction arg, boolean bl) {
        return serverLevel.getShade(arg, bl);
    }

    @Override
    public @NotNull LevelLightEngine getLightEngine() {
        return serverLevel.getLightEngine();
    }

    @Override
    public int getBrightness(@NotNull LightLayer arg, @NotNull BlockPos arg2) {
        return serverLevel.getBrightness(arg, arg2);
    }

    @Override
    public int getRawBrightness(@NotNull BlockPos arg, int i) {
        return serverLevel.getRawBrightness(arg, i);
    }

    @Override
    public boolean canSeeSky(@NotNull BlockPos arg) {
        return serverLevel.canSeeSky(arg);
    }

    @Override
    public @NotNull WorldBorder getWorldBorder() {
        return serverLevel.getWorldBorder();
    }

    @Override
    public boolean isUnobstructed(@NotNull BlockState arg, @NotNull BlockPos arg2, @NotNull CollisionContext arg3) {
        return serverLevel.isUnobstructed(arg, arg2, arg3);
    }

    @Override
    public boolean isUnobstructed(@NotNull Entity arg) {
        return serverLevel.isUnobstructed(arg);
    }

    @Override
    public boolean noCollision(@NotNull AABB arg) {
        return serverLevel.noCollision(arg);
    }

    @Override
    public boolean noCollision(@NotNull Entity arg) {
        return serverLevel.noCollision(arg);
    }

    @Override
    public boolean noCollision(@Nullable Entity arg, @NotNull AABB arg2) {
        return serverLevel.noCollision(arg, arg2);
    }

    @Override
    public @NotNull Iterable<VoxelShape> getCollisions(@Nullable Entity arg, @NotNull AABB arg2) {
        return serverLevel.getCollisions(arg, arg2);
    }

    @Override
    public @NotNull Iterable<VoxelShape> getBlockCollisions(@Nullable Entity arg, @NotNull AABB arg2) {
        return serverLevel.getBlockCollisions(arg, arg2);
    }

    @Override
    public boolean collidesWithSuffocatingBlock(@Nullable Entity arg, @NotNull AABB arg2) {
        return serverLevel.collidesWithSuffocatingBlock(arg, arg2);
    }

    @Override
    public @NotNull Optional<BlockPos> findSupportingBlock(@NotNull Entity arg, @NotNull AABB arg2) {
        return serverLevel.findSupportingBlock(arg, arg2);
    }

    @Override
    public @NotNull Optional<Vec3> findFreePosition(@Nullable Entity arg, @NotNull VoxelShape arg2, @NotNull Vec3 arg3, double d, double e, double f) {
        return serverLevel.findFreePosition(arg, arg2, arg3, d, e, f);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(@NotNull BlockPos arg) {
        return serverLevel.getBlockEntity(arg);
    }

    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos arg) {
        return serverLevel.getBlockState(arg);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos arg) {
        return serverLevel.getFluidState(arg);
    }

    @Override
    public int getLightEmission(@NotNull BlockPos arg) {
        return serverLevel.getLightEmission(arg);
    }

    @Override
    public int getMaxLightLevel() {
        return serverLevel.getMaxLightLevel();
    }

    @Override
    public @NotNull Stream<BlockState> getBlockStates(@NotNull AABB arg) {
        return serverLevel.getBlockStates(arg);
    }

    @Override
    public @NotNull BlockHitResult isBlockInLine(@NotNull ClipBlockStateContext arg) {
        return serverLevel.isBlockInLine(arg);
    }

    @Override
    public @NotNull BlockHitResult clip(@NotNull ClipContext arg) {
        return serverLevel.clip(arg);
    }

    @Override
    public @Nullable BlockHitResult clipWithInteractionOverride(@NotNull Vec3 arg, @NotNull Vec3 arg2, @NotNull BlockPos arg3, @NotNull VoxelShape arg4, @NotNull BlockState arg5) {
        return serverLevel.clipWithInteractionOverride(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public double getBlockFloorHeight(@NotNull VoxelShape arg, @NotNull Supplier<VoxelShape> supplier) {
        return serverLevel.getBlockFloorHeight(arg, supplier);
    }

    @Override
    public double getBlockFloorHeight(@NotNull BlockPos arg) {
        return serverLevel.getBlockFloorHeight(arg);
    }

    @Override
    public @NotNull List<Entity> getEntities(@Nullable Entity arg, @NotNull AABB arg2, @NotNull Predicate<? super Entity> predicate) {
        return serverLevel.getEntities(arg, arg2, predicate);
    }

    @Override
    public <T extends Entity> @NotNull List<T> getEntities(@NotNull EntityTypeTest<Entity, T> arg, @NotNull AABB arg2, @NotNull Predicate<? super T> predicate) {
        return serverLevel.getEntities(arg, arg2, predicate);
    }

    @Override
    public <T extends Entity> @NotNull List<T> getEntitiesOfClass(@NotNull Class<T> class_, @NotNull AABB arg, @NotNull Predicate<? super T> predicate) {
        return serverLevel.getEntitiesOfClass(class_, arg, predicate);
    }

    @Override
    public @NotNull List<? extends Player> players() {
        return serverLevel.players();
    }

    @Override
    public @NotNull List<Entity> getEntities(@Nullable Entity arg, @NotNull AABB arg2) {
        return serverLevel.getEntities(arg, arg2);
    }

    @Override
    public <T extends Entity> @NotNull List<T> getEntitiesOfClass(@NotNull Class<T> class_, @NotNull AABB arg) {
        return serverLevel.getEntitiesOfClass(class_, arg);
    }

    @Override
    public @Nullable Player getNearestPlayer(double d, double e, double f, double g, @Nullable Predicate<Entity> predicate) {
        return serverLevel.getNearestPlayer(d, e, f, g, predicate);
    }

    @Override
    public @Nullable Player getNearestPlayer(@NotNull Entity arg, double d) {
        return serverLevel.getNearestPlayer(arg, d);
    }

    @Override
    public @Nullable Player getNearestPlayer(double d, double e, double f, double g, boolean bl) {
        return serverLevel.getNearestPlayer(d, e, f, g, bl);
    }

    @Override
    public boolean hasNearbyAlivePlayer(double d, double e, double f, double g) {
        return serverLevel.hasNearbyAlivePlayer(d, e, f, g);
    }

    @Override
    public @Nullable Player getNearestPlayer(@NotNull TargetingConditions arg, @NotNull LivingEntity arg2) {
        return serverLevel.getNearestPlayer(arg, arg2);
    }

    @Override
    public @Nullable Player getNearestPlayer(@NotNull TargetingConditions arg, @NotNull LivingEntity arg2, double d, double e, double f) {
        return serverLevel.getNearestPlayer(arg, arg2, d, e, f);
    }

    @Override
    public @Nullable Player getNearestPlayer(@NotNull TargetingConditions arg, double d, double e, double f) {
        return serverLevel.getNearestPlayer(arg, d, e, f);
    }

    @Override
    public @Nullable <T extends LivingEntity> T getNearestEntity(@NotNull Class<? extends T> class_, @NotNull TargetingConditions arg, @Nullable LivingEntity arg2, double d, double e, double f, @NotNull AABB arg3) {
        return serverLevel.getNearestEntity(class_, arg, arg2, d, e, f, arg3);
    }

    @Override
    public @Nullable <T extends LivingEntity> T getNearestEntity(@NotNull List<? extends T> list, @NotNull TargetingConditions arg, @Nullable LivingEntity arg2, double d, double e, double f) {
        return serverLevel.getNearestEntity(list, arg, arg2, d, e, f);
    }

    @Override
    public @NotNull List<Player> getNearbyPlayers(@NotNull TargetingConditions arg, @NotNull LivingEntity arg2, @NotNull AABB arg3) {
        return serverLevel.getNearbyPlayers(arg, arg2, arg3);
    }

    @Override
    public <T extends LivingEntity> @NotNull List<T> getNearbyEntities(@NotNull Class<T> class_, @NotNull TargetingConditions arg, @NotNull LivingEntity arg2, @NotNull AABB arg3) {
        return serverLevel.getNearbyEntities(class_, arg, arg2, arg3);
    }

    @Override
    public @Nullable Player getPlayerByUUID(@NotNull UUID uUID) {
        return serverLevel.getPlayerByUUID(uUID);
    }

    @Override
    public boolean isStateAtPosition(@NotNull BlockPos arg, @NotNull Predicate<BlockState> predicate) {
        return serverLevel.isStateAtPosition(arg, predicate);
    }

    @Override
    public boolean isFluidAtPosition(@NotNull BlockPos arg, @NotNull Predicate<FluidState> predicate) {
        return serverLevel.isFluidAtPosition(arg, predicate);
    }

    @Override
    public boolean setBlock(@NotNull BlockPos rawPos, BlockState state, int i, int j) {
        BlockState originalState = serverLevel.getBlockState(rawPos);
        boolean result = serverLevel.setBlock(rawPos, state, i, j);
        BlockPos pos = rawPos.immutable();
        AtomicInteger index = new AtomicInteger();
        AtomicInteger paletteId = new AtomicInteger();
        if (!palettes.isEmpty()) {
            Optional<Integer> chosen = palettes.stream().filter(palette -> {
                Optional<BlockState> found = palette.stream().filter(s -> s == state).findAny();
                index.set(found.map(palette::indexOf).orElse(index.get()));
                return found.isPresent();
            }).findFirst().map(palettes::indexOf);
            chosen.ifPresentOrElse(paletteId::set, () -> {
                List<BlockState> palette = palettes.get(palettes.size() - 1);
                if (palette.size() < 16) {
                    index.set(palette.size());
                    paletteId.set(palettes.size() - 1);
                    palette.add(state);
                } else {
                    index.set(0);
                    paletteId.set(palette.size());
                    List<BlockState> newList = new ArrayList<>();
                    newList.add(state);
                    palettes.add(newList);
                }
            });
        } else {
            List<BlockState> newList = new ArrayList<>();
            newList.add(state);
            palettes.add(newList);
        }
        PBECStructure.BlockUpdateData toUpdate = new PBECStructure.BlockUpdateData(pos, state, paletteId.get(), index.get(), Optional.empty());
        toEmitTo.add(toUpdate);
        resetRunners.offerFirst(() -> {
            Optional.ofNullable(serverLevel.getBlockEntity(pos)).ifPresent(entity -> {
                toUpdate.setTag(entity.saveWithId());
                entity.setRemoved();
                Clearable.tryClear(entity);
            });
            serverLevel.setBlock(pos, originalState, 3);
        });
        return result;
    }

    @Override
    public boolean removeBlock(@NotNull BlockPos pos, boolean bl) {
        FluidState fluidstate = this.getFluidState(pos);
        return this.setBlock(pos, fluidstate.createLegacyBlock(), 3 | (bl ? 64 : 0));
    }

    @Override
    public boolean destroyBlock(@NotNull BlockPos pos, boolean bl, @Nullable Entity arg2, int i) {
        BlockState blockstate = this.getBlockState(pos);
        if (blockstate.isAir()) {
            return false;
        } else {
            FluidState fluidstate = this.getFluidState(pos);
            if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
                this.levelEvent(2001, pos, Block.getId(blockstate));
            }

            if (bl) {
                BlockEntity blockentity = blockstate.hasBlockEntity() ? this.getBlockEntity(pos) : null;
                Block.dropResources(blockstate, serverLevel, pos, blockentity, arg2, ItemStack.EMPTY);
            }

            boolean succeeded = this.setBlock(pos, fluidstate.createLegacyBlock(), 3, i);
            if (succeeded) {
                this.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(arg2, blockstate));
            }

            return succeeded;
        }
    }

    @Override
    public boolean addFreshEntity(@NotNull Entity arg) {
        return entities.add(arg);
    }

    @Override
    public <T extends BlockEntity> @NotNull Optional<T> getBlockEntity(@NotNull BlockPos arg, @NotNull BlockEntityType<T> arg2) {
        return serverLevel.getBlockEntity(arg, arg2);
    }

    @Override
    public @NotNull List<VoxelShape> getEntityCollisions(@Nullable Entity arg, @NotNull AABB arg2) {
        return serverLevel.getEntityCollisions(arg, arg2);
    }

    @Override
    public boolean isUnobstructed(@Nullable Entity arg, @NotNull VoxelShape arg2) {
        return serverLevel.isUnobstructed(arg, arg2);
    }

    @Override
    public @NotNull BlockPos getHeightmapPos(Heightmap.@NotNull Types arg, @NotNull BlockPos arg2) {
        return serverLevel.getHeightmapPos(arg, arg2);
    }

    @Override
    public int getDirectSignal(@NotNull BlockPos arg, @NotNull Direction arg2) {
        return serverLevel.getDirectSignal(arg, arg2);
    }

    @Override
    public int getDirectSignalTo(@NotNull BlockPos arg) {
        return serverLevel.getDirectSignalTo(arg);
    }

    @Override
    public int getControlInputSignal(@NotNull BlockPos arg, @NotNull Direction arg2, boolean bl) {
        return serverLevel.getControlInputSignal(arg, arg2, bl);
    }

    @Override
    public boolean hasSignal(@NotNull BlockPos arg, @NotNull Direction arg2) {
        return serverLevel.hasSignal(arg, arg2);
    }

    @Override
    public int getSignal(@NotNull BlockPos arg, @NotNull Direction arg2) {
        return serverLevel.getSignal(arg, arg2);
    }

    @Override
    public boolean hasNeighborSignal(@NotNull BlockPos arg) {
        return serverLevel.hasNeighborSignal(arg);
    }

    @Override
    public int getBestNeighborSignal(@NotNull BlockPos arg) {
        return serverLevel.getBestNeighborSignal(arg);
    }
}
