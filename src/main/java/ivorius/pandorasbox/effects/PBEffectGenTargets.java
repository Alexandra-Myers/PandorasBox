/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.spawn_entities.SpawnEntityIDListEffect;
import ivorius.pandorasbox.effects.structure.StructureTarget;
import ivorius.pandorasbox.effects.structure.TargetConfiguration;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.PandoraBlockTags;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTargets extends PBEffectGenerateByStructure<StructureTarget> {
    public static final MapCodec<PBEffectGenTargets> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            PBNBTHelper.arrayCodec(StructureTarget.CODEC, () -> new StructureTarget[0]).fieldOf("structures").forGetter(PBEffectGenTargets::getStructures),
                            WeightedEntity.ID_CODEC.fieldOf("entity_to_spawn").forGetter(PBEffectGenTargets::getEntityToSpawn),
                            Codec.DOUBLE.fieldOf("range").forGetter(PBEffectGenTargets::getRange),
                            Codec.DOUBLE.fieldOf("target_size").forGetter(PBEffectGenTargets::getTargetSize),
                            Codec.DOUBLE.fieldOf("entity_density").forGetter(PBEffectGenTargets::getEntityDensity))
                    .apply(instance, PBEffectGenTargets::new));
    public String entityToSpawn;
    public double range;
    public double targetSize;
    public double entityDensity;
    public PBEffectGenTargets(int maxTicksAlive, StructureTarget[] structures, String entityToSpawn, double range, double targetSize, double entityDensity) {
        super(maxTicksAlive);
        this.structures = structures;
        this.entityToSpawn = entityToSpawn;
        this.range = range;
        this.targetSize = targetSize;
        this.entityDensity = entityDensity;
    }
    public PBEffectGenTargets(int maxTicksAlive, String entityToSpawn, double range, double targetSize, double entityDensity) {
        this(maxTicksAlive, new StructureTarget[0], entityToSpawn, range, targetSize, entityDensity);
    }

    public String getEntityToSpawn() {
        return entityToSpawn;
    }

    public double getRange() {
        return range;
    }

    public double getTargetSize() {
        return targetSize;
    }

    public double getEntityDensity() {
        return entityDensity;
    }

    public void createTargets(Level world, double x, double y, double z, RandomSource random) {
        List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));
        this.structures = new StructureTarget[players.size()];

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            StructureTarget structureTarget = new StructureTarget();
            structureTarget.pos = new Vec3i(Mth.floor(player.getX()), Mth.floor(player.getY() - 0.5), Mth.floor(player.getZ()));
            structureTarget.structureStart = random.nextFloat() * 0.3f;
            structureTarget.structureLength = 0.5f + random.nextFloat() * 0.2f;

            Integer[] colors = new Integer[Mth.ceil(targetSize) * 2];
            for (int j = 0; j < colors.length; j++) {
                colors[j] = random.nextInt(16);
            }
            structureTarget.configuration = new TargetConfiguration(colors);

            structures[i] = structureTarget;
        }
    }

    @Override
    public void generateStructure(Level level, PandorasBoxEntity entity, RandomSource random, StructureTarget structure, BlockPos pos, float newRatio, float prevRatio) {
        double newRange = newRatio * targetSize;
        double prevRange = prevRatio * targetSize;

        int requiredRange = Mth.ceil(newRange);

        for (int xP = -requiredRange; xP <= requiredRange; xP++) {
            for (int zP = -requiredRange; zP <= requiredRange; zP++) {
                double dist = Mth.sqrt(xP * xP + zP * zP);

                if (dist < newRange) {
                    if (dist >= prevRange) {
                        HolderSet.Named<Block> terracottas = BuiltInRegistries.BLOCK.getTag(PandoraBlockTags.ALL_TERRACOTTA).orElseThrow();
                        Vec3i offset = structure.pos.offset(xP, 0, zP);
                        setBlockSafe(level, new BlockPos(offset), terracottas.get(structure.getColors()[Mth.floor(dist)]).value().defaultBlockState());

                        double nextDist = Mth.sqrt((xP * xP + 3 * 3) + (zP * zP + 3 * 3));

                        if (nextDist >= targetSize && random.nextDouble() < entityDensity) {
                            Entity[] toAdd = SpawnEntityIDListEffect.createEntity(level, entity, random, entityToSpawn, offset.getX() + 0.5, offset.getY() + 1.5, offset.getZ() + 0.5);
                            Entity previousEntity = null;
                            for (Entity newEntity : toAdd) {
                                if (newEntity != null) {
                                    level.addFreshEntity(newEntity);
                                    if (previousEntity != null) previousEntity.startRiding(newEntity, true);
                                    previousEntity = newEntity;
                                }
                            }
                        }
                    }
                }

                for (int yP = 1; yP <= requiredRange; yP++) {
                    double dist3D = Mth.sqrt(xP * xP + zP * zP + yP * yP);

                    if (dist3D < newRange && dist3D >= prevRange) // -3 so we have a bit of a height bonus
                        setBlockToAirSafe(level, new BlockPos(structure.pos.offset(xP, yP, zP)));
                }
            }
        }
    }

    @Override
    public StructureTarget createStructure() {
        return new StructureTarget();
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
