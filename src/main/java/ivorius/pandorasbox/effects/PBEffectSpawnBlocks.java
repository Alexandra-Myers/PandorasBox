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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnBlocks extends PBEffectSpawnEntities {
    public Block[] blocks;
    public PBEffectSpawnBlocks() {}

    public PBEffectSpawnBlocks(int time, Block[] blocks, boolean comesFromBox) {
        super(time, blocks.length);

        setSpawnsFromBox(comesFromBox);
        setDoesSpawnDirect(0.1, 0.4, 0.2, 1.0);
        this.blocks = blocks;
    }

    public PBEffectSpawnBlocks(int time, double range, double shiftY, Block[] blocks) {
        super(time, blocks.length);

        setDoesNotSpawnDirect(range, shiftY);
        this.blocks = blocks;
    }

    @Override
    public Entity spawnEntity(Level world, PandorasBoxEntity entity, RandomSource random, int number, double x, double y, double z) {
        if(world.isClientSide()) return null;
        Block block = blocks[number];

        return FallingBlockEntity.fall(world, BlockPos.containing(x, y, z), block.defaultBlockState());
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
