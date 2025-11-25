/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.structure.ShapeConfiguration;
import ivorius.pandorasbox.effects.structure.StructureShape;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.PBEffectInit;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenShapes extends PBEffectGenerateByStructure<StructureShape> {
    public static final MapCodec<PBEffectGenShapes> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            PBNBTHelper.arrayCodec(StructureShape.CODEC, () -> new StructureShape[0]).fieldOf("structures").forGetter(PBEffectGenShapes::getStructures))
                    .apply(instance, PBEffectGenShapes::new));
    public PBEffectGenShapes(int maxTicksAlive) {
        this(maxTicksAlive, new StructureShape[0]);
    }

    public PBEffectGenShapes(int maxTicksAlive, StructureShape[] structures) {
        super(maxTicksAlive);
        this.structures = structures;
    }

    public void setRandomShapes(RandomSource random, Collection<WeightedBlock> blocks, double range, double minSize, double maxSize, int number, int shape) {
        structures = new StructureShape[number];

        for (int i = 0; i < structures.length; i++) {
            StructureShape randomShape = createStructure();
            applyRandomProperties(randomShape, range, random);
            randomShape.configuration = new ShapeConfiguration(PandorasBoxHelper.getRandomBlockList(random, blocks), shape < 0 ? random.nextInt(4) : shape, minSize + random.nextDouble() * (maxSize - minSize));

            structures[i] = randomShape;
        }
    }

    public void setShapes(RandomSource random, Block[] blockSelection, double range, double minSize, double maxSize, int number, int shape, int unifiedSeed) {
        structures = new StructureShape[number];

        for (int i = 0; i < structures.length; i++) {
            StructureShape randomShape = createStructure();
            applyRandomProperties(randomShape, range, random);
            randomShape.configuration = new ShapeConfiguration(blockSelection.clone(), shape < 0 ? random.nextInt(4) : shape, minSize + random.nextDouble() * (maxSize - minSize));
            randomShape.unifiedSeed = unifiedSeed;

            structures[i] = randomShape;
        }
    }

    @Override
    public void generateStructure(Level level, PandorasBoxEntity entity, RandomSource random, StructureShape structure, BlockPos pos, float newRatio, float prevRatio) {
        double prevSize = structure.getSize() * prevRatio;
        double newSize = structure.getSize() * newRatio;
        int requiredRange = Mth.floor(newSize);

        switch (structure.getShapeType()) {
            case 0 -> {
                for (int xPlus = -requiredRange; xPlus <= requiredRange; xPlus++) {
                    for (int yPlus = -requiredRange; yPlus <= requiredRange; yPlus++) {
                        for (int zPlus = -requiredRange; zPlus <= requiredRange; zPlus++) {
                            double dist = Mth.sqrt(xPlus * xPlus + yPlus * yPlus + zPlus * zPlus);

                            if (dist <= newSize) {
                                if (dist > prevSize) {
                                    generateOnBlock(level, entity, random, structure, pos.offset(structure.pos.offset(xPlus, yPlus, zPlus)));
                                } else {
                                    zPlus = -zPlus; // We can skip all blocks in between
                                }
                            }
                        }
                    }
                }
            }
            case 1 -> {
                for (int xPlus = -requiredRange; xPlus <= requiredRange; xPlus++) {
                    for (int yPlus = -requiredRange; yPlus <= requiredRange; yPlus++) {
                        for (int zPlus = -requiredRange; zPlus <= requiredRange; zPlus++) {
                            double xDist = Math.abs(xPlus);
                            double yDist = Math.abs(yPlus);
                            double zDist = Math.abs(zPlus);

                            if (xDist <= newSize && yDist <= newSize && zDist <= newSize) {
                                if (xDist > prevSize || yDist > prevSize || zDist > prevSize) {
                                    generateOnBlock(level, entity, random, structure, pos.offset(structure.pos.offset(xPlus, yPlus, zPlus)));
                                } else {
                                    zPlus = -zPlus; // We can skip all blocks in between
                                }
                            }
                        }
                    }
                }
            }
            default -> {
                int totalHeight = Mth.floor(structure.getSize());

                for (int yPlus = -requiredRange; yPlus <= requiredRange; yPlus++) {
                    int yDist = Math.abs(yPlus);

                    if (yDist <= newSize) {
                        if (yDist > prevSize) {
                            int levelSize = structure.getShapeType() == 2 ? (totalHeight - yDist) : (yDist + 1);

                            for (int xPlus = -levelSize; xPlus <= levelSize; xPlus++) {
                                for (int zPlus = -levelSize; zPlus <= levelSize; zPlus++) {
                                    generateOnBlock(level, entity, random, structure, pos.offset(structure.pos.offset(xPlus, yPlus, zPlus)));
                                }
                            }
                        } else {
                            yPlus = -yPlus; // We can skip all blocks in between
                        }
                    }
                }
            }
        }
    }

    public void generateOnBlock(Level world, PandorasBoxEntity entity, RandomSource random, StructureShape structure, BlockPos pos) {
        Block block = structure.getBlocks()[random.nextInt(structure.getBlocks().length)];
        setBlockVarying(world, pos, block, structure.unifiedSeed);
    }

    @Override
    public StructureShape createStructure() {
        return new StructureShape();
    }


    @Override
    public @NotNull PBEffectType<? extends PBEffect> type() {
        return PBEffectInit.GEN_SHAPES;
    }
}
