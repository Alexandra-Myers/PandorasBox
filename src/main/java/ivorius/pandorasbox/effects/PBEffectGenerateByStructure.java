/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.structure.Structure;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectGenerateByStructure<T extends Structure> extends PBEffectNormal {
    public T[] structures;
    public PBEffectGenerateByStructure(int maxTicksAlive) {
        super(maxTicksAlive);
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (!level.isClientSide()) {
            for (T structure : structures) {
                float newStructureRatio = getStructureRatio(newRatio, structure);
                float prevStructureRatio = getStructureRatio(prevRatio, structure);

                int baseX = Mth.floor(effectCenter.x);
                int baseY = Mth.floor(effectCenter.y);
                int baseZ = Mth.floor(effectCenter.z);

                if (newStructureRatio > prevStructureRatio) {
                    generateStructure(level, entity, random, structure, new BlockPos(baseX, baseY, baseZ), newStructureRatio, prevStructureRatio);
                }
            }
        }
    }

    private float getStructureRatio(float ratio, Structure structure) {
        return Mth.clamp((ratio - structure.structureStart) / structure.structureLength, 0.0f, 1.0f);
    }

    public T[] getStructures() {
        return structures;
    }

    public abstract void generateStructure(Level level, PandorasBoxEntity entity, RandomSource random, T structure, BlockPos pos, float newRatio, float prevRatio);

    public abstract T createStructure();

    public static void applyRandomProperties(Structure structure, double range, RandomSource random) {
        structure.structureLength = random.nextFloat() * 0.8f + 0.1f;
        structure.structureStart = random.nextFloat() * (1.0f - structure.structureLength);

        structure.pos = new Vec3i(Mth.floor((random.nextDouble() - random.nextDouble()) * range), Mth.floor((random.nextDouble() - random.nextDouble()) * range), Mth.floor((random.nextDouble() - random.nextDouble()) * range));

        structure.unifiedSeed = PandorasBoxHelper.getRandomUnifiedSeed(random);
    }

}
