/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTransform extends PBEffectGenerate {
    public Block[] blocks;
    public PBEffectGenTransform() {}

    public PBEffectGenTransform(int time, double range, int unifiedSeed, Block[] blocks) {
        super(time, range, 1, unifiedSeed);

        this.blocks = blocks;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide) {
            Block block = blocks[random.nextInt(blocks.length)];

            if (world.loadedAndEntityCanStandOn(pos, entity)) {
                setBlockVarying(world, pos, block, unifiedSeed);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
    }
}
