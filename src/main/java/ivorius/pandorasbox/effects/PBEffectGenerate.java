/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

import static ivorius.pandorasbox.effects.PBEffectGenConvertToNether.makeResolver;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerate extends PBEffectRangeBased
{
    public int unifiedSeed;
    public boolean biomeUnchanged = true;
    public PBEffectGenerate() {}

    public PBEffectGenerate(int time, double range, int passes, int unifiedSeed)
    {
        super(time, range, passes);

        this.unifiedSeed = unifiedSeed;
    }
    public static Vec3i floorAll(double x, double y, double z) {
        return new Vec3i(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }
    public void changeBiome(ResourceKey<Biome> biomeResourceKey, int pass, Vec3 effectCenter, ServerLevel serverLevel) {
        if(!biomeUnchanged) return;
        BoundingBox boundingbox = BoundingBox.fromCorners(floorAll(range + (passes - 1) * 5.0 + effectCenter.x, range + (passes - 1) * 5.0 + effectCenter.y, range + (passes - 1) * 5.0 + effectCenter.z), floorAll(effectCenter.x - range + (passes - 1) * 5.0, effectCenter.y - range + (passes - 1) * 5.0, effectCenter.z - range + (passes - 1) * 5.0));
        ArrayList<ChunkAccess> chunks = new ArrayList<>();
        for (int k = SectionPos.blockToSectionCoord(boundingbox.minZ()); k <= SectionPos.blockToSectionCoord(boundingbox.maxZ()); ++k) {
            for (int l = SectionPos.blockToSectionCoord(boundingbox.minX()); l <= SectionPos.blockToSectionCoord(boundingbox.maxX()); ++l) {
                ChunkAccess chunkAccess = serverLevel.getChunk(l, k, ChunkStatus.FULL, false);
                if (chunkAccess != null)
                    chunks.add(chunkAccess);
            }
        }
        for (ChunkAccess chunkAccess : chunks) {
            Registry<Biome> biomeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME);
            Biome biome = biomeRegistry.get(biomeResourceKey);
            assert biome != null;
            chunkAccess.fillBiomesFromNoise(makeResolver(biomeRegistry.wrapAsHolder(biome)), serverLevel.getChunkSource().randomState().sampler());
            chunkAccess.setUnsaved(true);
        }

        serverLevel.getChunkSource().chunkMap.resendBiomesForChunks(chunks);
        biomeUnchanged = false;

    }

    @Override
    public void generateInRange(Level world, PandorasBoxEntity entity, RandomSource random, Vec3 effectCenter, double prevRange, double newRange, int pass)
    {
        int requiredRange = Mth.ceil(newRange);

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        for (int x = -requiredRange; x <= requiredRange; x++)
        {
            for (int y = -requiredRange; y <= requiredRange; y++)
            {
                for (int z = -requiredRange; z <= requiredRange; z++)
                {
                    double dist = Mth.sqrt(x * x + y * y + z * z);

                    if (dist <= newRange)
                    {
                        if (dist > prevRange)
                            generateOnBlock(world, entity, effectCenter, random, pass, new BlockPos(baseX + x, baseY + y, baseZ + z), dist);
                        else
                            z = -z; // We can skip all blocks in between
                    }
                }
            }
        }
    }

    public abstract void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range);

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);
        compound.putInt("unifiedSeed", unifiedSeed);
        compound.putBoolean("biomeUnchanged", biomeUnchanged);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);
        unifiedSeed = compound.getInt("unifiedSeed");
        biomeUnchanged = compound.getBoolean("biomeUnchanged");
    }
}
