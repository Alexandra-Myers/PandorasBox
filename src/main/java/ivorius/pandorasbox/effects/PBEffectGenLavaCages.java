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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenLavaCages extends PBEffectGenerate
{
    public Block lavaBlock;
    public Block fillBlock;
    public Block cageBlock;
    public Block floorBlock;
    public Integer heightOffset = null;
    public Integer wallDist = null;
    public PBEffectGenLavaCages() {}

    public PBEffectGenLavaCages(int time, double range, int unifiedSeed, Block lavaBlock, Block cageBlock, Block fillBlock, Block floorBlock) {
        super(time, range, 1, unifiedSeed);

        this.lavaBlock = lavaBlock;
        this.cageBlock = cageBlock;
        this.fillBlock = fillBlock;
        this.floorBlock = floorBlock;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if(heightOffset == null) {
            heightOffset = random.nextInt(10) + 2;
        }
        if(wallDist == null) {
            wallDist = random.nextInt(5) + 2;
        }
        if (!world.isClientSide()) {
            if (!world.loadedAndEntityCanStandOn(pos, entity)) {
                List<Player> outerList = world.getEntitiesOfClass(Player.class, BlockPositions.expandToAABB(pos, 3.5, 3.5, 3.5));

                if (!outerList.isEmpty()) {
                    for(Player player : outerList) {
                        int playerY = player.blockPosition().getY();
                        int playerX = player.blockPosition().getX();
                        int playerZ = player.blockPosition().getZ();
                        int floor = playerY - 1;
                        int ceil = playerY + heightOffset;
                        createFloorOrCeil(world, playerX, floor, playerZ, wallDist);
                        createFloorOrCeil(world, playerX, ceil, playerZ, wallDist);
                        createWalls(world, playerX, playerY, ceil, playerZ, wallDist);
                        fill(world, playerX, playerY, ceil, playerZ, wallDist);
                    }
                }
            }
        }
    }
    public void createFloorOrCeil(Level world, int originX, int y, int originZ, int offset) {
        if (floorBlock == null)
            floorBlock = Blocks.OBSIDIAN;
        for (int x = originX - offset; x <= originX + offset; x++) {
            for(int z = originZ - offset; z <= originZ + offset; z++) {
                setBlockVaryingUnsafeSrc(world, new BlockPos(x, y, z), floorBlock, unifiedSeed);
            }
        }
    }
    public void createWalls(Level world, int originX, int originY, int ceilheight, int originZ, int offset) {
        for (int x = originX - offset; x <= originX + offset; x++) {
            for (int y = originY; y < ceilheight; y++) {
                for(int z = originZ - offset; z <= originZ + offset; z++) {
                    if(x == originX - offset || x == originX + offset || z == originZ - offset || z == originZ + offset) {
                        setBlockVarying(world, new BlockPos(x, y, z), cageBlock, unifiedSeed);
                    }
                }
            }
        }
    }
    public void fill(Level world, int originX, int originY, int ceilheight, int originZ, int offset) {
        offset -= 1;
        boolean fill = fillBlock != null;
        for (int x = originX - offset; x <= originX + offset; x++) {
            for (int y = originY; y < ceilheight - 1; y++) {
                for(int z = originZ - offset; z <= originZ + offset; z++) {
                    setBlockVarying(world, new BlockPos(x, y, z), fill ? fillBlock : lavaBlock, unifiedSeed);
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        if (lavaBlock != null)
            compound.putString("lavaBlock", PBNBTHelper.storeBlockString(lavaBlock));
        if (fillBlock != null)
            compound.putString("fillBlock", PBNBTHelper.storeBlockString(fillBlock));

        compound.putString("cageBlock", PBNBTHelper.storeBlockString(cageBlock));

        compound.putString("floorBlock", PBNBTHelper.storeBlockString(floorBlock));
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        lavaBlock = PBNBTHelper.getBlock(compound.getString("lavaBlock"));
        fillBlock = PBNBTHelper.getBlock(compound.getString("fillBlock"));
        cageBlock = PBNBTHelper.getBlock(compound.getString("cageBlock"));
        floorBlock = PBNBTHelper.getBlock(compound.getString("floorBlock"));
    }
}
