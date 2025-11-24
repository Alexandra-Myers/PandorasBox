/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.PBEffectInit;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenerate extends PBEffectRangeBased {
    public static final MapCodec<PBEffectGenerate> CODEC = RecordCodecBuilder.mapCodec(instance ->
            PBEffectRangeBased.baseFields(instance)
                    .and(GenerateEffect.CODEC.fieldOf("effect").forGetter(PBEffectGenerate::getGenerateEffect))
                    .apply(instance, PBEffectGenerate::new));
    public final GenerateEffect generateEffect;

    public PBEffectGenerate(int time, double range, int passes, int unifiedSeed, GenerateEffect generateEffect) {
        super(time, range, passes, unifiedSeed);
        this.generateEffect = generateEffect;
    }

    public GenerateEffect getGenerateEffect() {
        return generateEffect;
    }

    public void changeBiome(ResourceKey<Biome> biomeResourceKey, int baseX, int baseY, int baseZ, ServerLevel serverLevel, List<ChunkAccess> chunks) {
        double range = this.range + (passes - 1) * 5.0;
        for (ChunkAccess chunkAccess : chunks) {
            Registry<Biome> biomeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME);
            Holder<Biome> biome = biomeRegistry.getHolderOrThrow(biomeResourceKey);
            chunkAccess.fillBiomesFromNoise((i, j, k, sampler) -> {
                int l = QuartPos.toBlock(i);
                int m = QuartPos.toBlock(j);
                int n = QuartPos.toBlock(k);
                Holder<Biome> holder2 = chunkAccess.getNoiseBiome(i, j, k);
                int x = l - baseX;
                int y = m - baseY;
                int z = n - baseZ;
                double dist = Mth.sqrt(x * x + y * y + z * z);

                if (dist <= range) return biome;
                else return holder2;
            }, serverLevel.getChunkSource().randomState().sampler());
            chunkAccess.setUnsaved(true);
        }

        serverLevel.getChunkSource().chunkMap.resendBiomesForChunks(chunks);
    }

    @Override
    public void generateInRange(Level level, PandorasBoxEntity entity, RandomSource random, Vec3 effectCenter, double prevRange, double newRange, int pass) {
        int requiredRange = Mth.ceil(newRange);

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        double newRatio = getRatioDone(entity.getTicksForEffect(this) + 1);

        for (int x = -requiredRange; x <= requiredRange; x++) {
            for (int y = -requiredRange; y <= requiredRange; y++) {
                for (int z = -requiredRange; z <= requiredRange; z++) {
                    double dist = Mth.sqrt(x * x + y * y + z * z);

                    if (dist <= newRange) {
                        if (dist > prevRange)
                            generateEffect.generateOnBlock(level, entity, effectCenter, random, pass, new BlockPos(baseX + x, baseY + y, baseZ + z), dist, newRatio, unifiedSeed);
                        else
                            z = -z; // We can skip all blocks in between
                    }
                }
            }
        }
    }

    @Override
    public void setUpEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        ResourceKey<Biome> biomeResourceKey = generateEffect.biome();

        if (biomeResourceKey != null && level instanceof ServerLevel serverLevel) {
            List<ChunkAccess> chunks = new ArrayList<>();
            int sectionRange = SectionPos.blockToSectionCoord(this.range + (passes - 1) * 5.0);
            int baseSectionX = SectionPos.blockToSectionCoord(baseX);
            int baseSectionZ = SectionPos.blockToSectionCoord(baseZ);
            for (int cz = -sectionRange; cz <= sectionRange; cz++)
                for (int cx = -sectionRange; cx <= sectionRange; cx++) {
                    ChunkAccess chunk = serverLevel.getChunk(cx + baseSectionX, cz + baseSectionZ, ChunkStatus.FULL, false);
                    if (chunk != null) chunks.add(chunk);
                }
            changeBiome(biomeResourceKey, baseX, baseY, baseZ, serverLevel, chunks);
        }
    }

    @Override
    public @NotNull PBEffectType<? extends PBEffect> type() {
        return PBEffectInit.GENERATE;
    }
}
