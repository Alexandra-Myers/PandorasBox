/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenPool extends PBEffectGenerate {
    public Block block;
    public Block platformBlock;
    public PBEffectGenPool() {}

    public PBEffectGenPool(int time, double range, int unifiedSeed, Block block, Block platformBlock) {
        super(time, range, 1, unifiedSeed);

        this.block = block;
        this.platformBlock = platformBlock;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide && !world.getBlockState(pos).isAir()) {
            boolean setPlatform = false;
            if (platformBlock != null && !platformBlock.defaultBlockState().isAir()) {
                List<LivingEntity> livingEntities = world.getEntitiesOfClass(LivingEntity.class, BlockPositions.expandToAABB(pos, 2.5, 2.5, 2.5));

                if (!livingEntities.isEmpty())
                    setPlatform = true;
            }

            if (setPlatform)
                setBlockVarying(world, pos, platformBlock, unifiedSeed);
            else
                setBlockVarying(world, pos, block, unifiedSeed);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);

        compound.putString("block", PBNBTHelper.storeBlockString(block));

        if (platformBlock != null)
            compound.putString("platformBlock", PBNBTHelper.storeBlockString(platformBlock));
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);

        block = PBNBTHelper.getBlock(compound.getString("block"));
        platformBlock = PBNBTHelper.getBlock(compound.getString("platformBlock"));
    }
}
