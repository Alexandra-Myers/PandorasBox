/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToRainbowCloth extends PBEffectGenerate {
    public int[] woolMetas;
    public double ringSize;
    public PBEffectGenConvertToRainbowCloth() {}

    public PBEffectGenConvertToRainbowCloth(int time, double range, int unifiedSeed, int[] woolMetas, double ringSize) {
        super(time, range, 1, unifiedSeed);
        this.woolMetas = woolMetas;
        this.ringSize = ringSize;
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return null;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (pass == 0) {
            if (world.loadedAndEntityCanStandOn(pos, entity)) {
                if (world.getBlockState(pos.above()).isAir()) {
                    double dist = Mth.sqrt((float) effectCenter.distanceToSqr(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)));
                    HolderSet.Named<Block> wool = BuiltInRegistries.BLOCK.getOrThrow(BlockTags.WOOL);
                    setBlockSafe(world, pos, wool.get(woolMetas[Mth.floor(dist / ringSize) % woolMetas.length]).value().defaultBlockState());
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putIntArray("woolMetas", woolMetas);
        compound.putDouble("ringSize", ringSize);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        woolMetas = compound.getIntArray("woolMetas");
        ringSize = compound.getDouble("ringSize");
    }
}
