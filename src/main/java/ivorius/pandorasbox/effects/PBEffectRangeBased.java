/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 31.03.14.
 */
public abstract class PBEffectRangeBased extends PBEffectNormal {
    public final int unifiedSeed;
    public final double range;
    public final int passes;

    public PBEffectRangeBased(int maxTicksAlive, double range, int passes, int unifiedSeed) {
        super(maxTicksAlive);
        this.range = range;
        this.passes = passes;
        this.unifiedSeed = unifiedSeed;
    }

    public int getUnifiedSeed() {
        return unifiedSeed;
    }

    public double getRange() {
        return range;
    }

    public int getPasses() {
        return passes;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        for (int i = 0; i < passes; i++) {
            double prevRange = getRange(prevRatio, i);
            double newRange = getRange(newRatio, i);

            generateInRange(level, entity, random, effectCenter, prevRange, newRange, i);
        }
    }

    protected double getRange(double ratio, int pass) {
        ratio = IvMathHelper.mixEaseInOut(0.0, 1.0, Math.sqrt(ratio));

        double fullRange = range + (passes - 1) * 5.0;
        double tempRange = ratio * fullRange - pass * 5.0;

        return Mth.clamp(tempRange, 0.0, range);
    }

    public abstract void generateInRange(Level level, PandorasBoxEntity entity, RandomSource random, Vec3 effectCenter, double prevRange, double newRange, int pass);
    public static <T extends PBEffectRangeBased> Products.P4<RecordCodecBuilder.Mu<T>, Integer, Double, Integer, Integer> baseFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(base(),
                Codec.DOUBLE.fieldOf("range").forGetter(PBEffectRangeBased::getRange),
                Codec.INT.fieldOf("passes").forGetter(PBEffectRangeBased::getPasses),
                Codec.INT.fieldOf("unified_seed").forGetter(PBEffectRangeBased::getUnifiedSeed));
    }
}
