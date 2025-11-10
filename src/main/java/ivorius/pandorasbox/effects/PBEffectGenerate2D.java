/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.two_dimensional.Generate2D;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenerate2D extends PBEffectRangeBased {
    public static final MapCodec<PBEffectGenerate2D> CODEC = RecordCodecBuilder.mapCodec(instance ->
            PBEffectRangeBased.baseFields(instance)
                    .and(Generate2D.CODEC.fieldOf("generate_two_dimensional").forGetter(PBEffectGenerate2D::getGenerate2D))
                    .apply(instance, PBEffectGenerate2D::new));
    public final Generate2D generate2D;
    public PBEffectGenerate2D(int time, double range, int passes, int unifiedSeed, Generate2D generate2D) {
        super(time, range, passes, unifiedSeed);
        this.generate2D = generate2D;
    }

    public Generate2D getGenerate2D() {
        return generate2D;
    }

    @Override
    public void generateInRange(Level level, PandorasBoxEntity entity, RandomSource random, Vec3 effectCenter, double prevRange, double newRange, int pass) {
        int requiredRange = Mth.floor(newRange);

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        for (int x = -requiredRange; x <= requiredRange; x++) {
            for (int z = -requiredRange; z <= requiredRange; z++) {
                double dist = Mth.sqrt(x * x + z * z);

                if (dist <= newRange) {
                    if (dist > prevRange) {
                        generate2D.generateOnSurface(level, entity, effectCenter, random, new BlockPos(baseX + x, baseY, baseZ + z), dist, range, pass, unifiedSeed);
                    } else {
                        z = -z; // We can skip all blocks in between
                    }
                }
            }
        }
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
