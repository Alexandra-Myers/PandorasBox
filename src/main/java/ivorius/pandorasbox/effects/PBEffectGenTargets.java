/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTargets extends PBEffectGenerateByStructure
{
    public String entityToSpawn;
    public double range;
    public double targetSize;
    public double entityDensity;
    public PBEffectGenTargets() {}

    public PBEffectGenTargets(int maxTicksAlive, String entityToSpawn, double range, double targetSize, double entityDensity)
    {
        super(maxTicksAlive);
        this.entityToSpawn = entityToSpawn;
        this.range = range;
        this.targetSize = targetSize;
        this.entityDensity = entityDensity;
    }

    public void createTargets(Level world, double x, double y, double z, RandomSource random)
    {
        List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));
        this.structures = new Structure[players.size()];

        for (int i = 0; i < players.size(); i++)
        {
            Player player = players.get(i);
            StructureTarget structureTarget = new StructureTarget();
            structureTarget.x = Mth.floor(player.getX());
            structureTarget.y = Mth.floor(player.getY() - 0.5);
            structureTarget.z = Mth.floor(player.getZ());
            structureTarget.structureStart = random.nextFloat() * 0.3f;
            structureTarget.structureLength = 0.5f + random.nextFloat() * 0.2f;

            structureTarget.colors = new int[Mth.ceil(targetSize) * 2];
            for (int j = 0; j < structureTarget.colors.length; j++)
            {
                structureTarget.colors[j] = random.nextInt(16);
            }

            structures[i] = structureTarget;
        }
    }

    public void generateStructure(Level world, PandorasBoxEntity entity, RandomSource random, Structure structure, BlockPos pos, float newRatio, float prevRatio)
    {
        if (world instanceof ServerLevel)
        {
            StructureTarget structureTarget = (StructureTarget) structure;

            double newRange = newRatio * targetSize;
            double prevRange = prevRatio * targetSize;

            int requiredRange = Mth.ceil(newRange);

            for (int xP = -requiredRange; xP <= requiredRange; xP++)
            {
                for (int zP = -requiredRange; zP <= requiredRange; zP++)
                {
                    double dist = Mth.sqrt(xP * xP + zP * zP);

                    if (dist < newRange)
                    {
                        if (dist >= prevRange)
                        {
                            ArrayListExtensions<Block> terracottas = new ArrayListExtensions<>();
                            terracottas.addAll(PandorasBox.stained_terracotta);
                            setBlockSafe(world, new BlockPos(structureTarget.x + xP, structureTarget.y, structureTarget.z + zP), terracottas.get(structureTarget.colors[Mth.floor(dist)]).defaultBlockState());

                            double nextDist = Mth.sqrt((xP * xP + 3 * 3) + (zP * zP + 3 * 3));

                            if (nextDist >= targetSize && random.nextDouble() < entityDensity)
                            {
                                Entity newEntity = PBEffectSpawnEntityIDList.createEntity(world, entity, random, entityToSpawn, structureTarget.x + xP + 0.5, structureTarget.y + 1.5, structureTarget.z + zP + 0.5);
                                assert newEntity != null;
                                world.addFreshEntity(newEntity);
                            }
                        }
                    }

                    for (int yP = 1; yP <= requiredRange; yP++)
                    {
                        double dist3D = Mth.sqrt(xP * xP + zP * zP + yP * yP);

                        if (dist3D < newRange && dist3D >= prevRange) // -3 so we have a bit of a height bonus
                        {
                            setBlockToAirSafe(world, new BlockPos(structureTarget.x + xP, structureTarget.y, structureTarget.z + zP));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putString("entityToSpawn", entityToSpawn);
        compound.putDouble("range", range);
        compound.putDouble("targetSize", targetSize);
        compound.putDouble("entityDensity", entityDensity);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        entityToSpawn = compound.getString("entityToSpawn");
        range = compound.getDouble("range");
        targetSize = compound.getDouble("targetSize");
        entityDensity = compound.getDouble("entityDensity");
    }

    @Override
    public StructureTarget createStructure()
    {
        return new StructureTarget();
    }

    public static class StructureTarget extends Structure
    {
        public int[] colors;

        public StructureTarget()
        {
        }

        @Override
        public void writeToNBT(CompoundTag compound)
        {
            super.writeToNBT(compound);

            compound.putIntArray("colors", colors);
        }

        @Override
        public void readFromNBT(CompoundTag compound)
        {
            super.readFromNBT(compound);

            colors = compound.getIntArray("colors");
        }
    }
}
