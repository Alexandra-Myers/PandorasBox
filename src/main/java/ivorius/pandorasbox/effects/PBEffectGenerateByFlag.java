/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.flags.GenerateByFlag;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.PBEffectInit;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenerateByFlag extends PBEffectRangeBased {
    public static final MapCodec<PBEffectGenerateByFlag> CODEC = RecordCodecBuilder.mapCodec(instance ->
            PBEffectRangeBased.baseFields(instance)
                    .and(GenerateByFlag.CODEC.fieldOf("generate_by_flag").forGetter(PBEffectGenerateByFlag::getGenerateByFlag))
                    .apply(instance, PBEffectGenerateByFlag::new));
    public final GenerateByFlag generateByFlag;

    public PBEffectGenerateByFlag(int time, double range, int passes, int unifiedSeed, GenerateByFlag generateByFlag) {
        super(time, range, passes, unifiedSeed);
        this.generateByFlag = generateByFlag;
    }

    public GenerateByFlag getGenerateByFlag() {
        return generateByFlag;
    }

    @Override
    public void setUpEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        super.setUpEffect(level, entity, effectCenter, random);

        byte requiredRange = (byte) Mth.ceil(range);

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        boolean[] flags = new boolean[31];

        for (byte x = (byte) -requiredRange; x <= requiredRange; x++) {
            for (byte z = (byte) -requiredRange; z <= requiredRange; z++) {
                for (byte y = (byte) -requiredRange; y <= requiredRange; y++) {
                    double dist = Mth.sqrt(x * x + y * y + z * z);

                    if (dist <= range)
                        flags[y + 15] = generateByFlag.hasFlag(level, entity, random, new BlockPos(baseX + x, baseY + y, baseZ + z));
                }

                setAllFlags(x, z, flags);
            }
        }
    }

    @Override
    public void generateInRange(Level level, PandorasBoxEntity entity, RandomSource random, Vec3 effectCenter, double prevRange, double newRange, int pass) {
        byte requiredRange = (byte) Mth.ceil(newRange);

        int baseX = Mth.floor(effectCenter.x);
        int baseY = Mth.floor(effectCenter.y);
        int baseZ = Mth.floor(effectCenter.z);

        for (byte x = (byte) -requiredRange; x <= requiredRange; x++) {
            for (byte y = (byte) -requiredRange; y <= requiredRange; y++) {
                for (byte z = (byte) -requiredRange; z <= requiredRange; z++) {
                    double dist = Mth.sqrt(x * x + y * y + z * z);

                    if (dist <= newRange) {
                        if (dist > prevRange) {
                            generateByFlag.generateOnBlock(level, entity, random, pass, unifiedSeed, new BlockPos(baseX + x, baseY + y, baseZ + z), dist, getFlag(x, y, z));
                        } else {
                            z = (byte) -z; // We can skip all blocks in between
                        }
                    }
                }
            }
        }
    }

    public void setAllFlags(byte x, byte z, boolean... flags) {
        int flagInt = 0;
        for (int i = flags.length - 1; i >= 0; i--) {
            boolean flag = flags[i];
            if (flag) {
                flagInt = (flagInt << 1) + 1;
            } else {
                flagInt = (flagInt << 1);
            }
        }

        this.generateByFlag.flags()[getFlagIndex(x, z)] = flagInt;
    }

    public void setFlag(byte x, byte y, byte z, boolean flag) {
        int index = getFlagIndex(x, z);
        int bit = getBitOfFlag(y);

        if (flag) {
            generateByFlag.flags()[index] = generateByFlag.flags()[index] | bit;
        } else {
            generateByFlag.flags()[index] = generateByFlag.flags()[index] & (~bit);
        }
    }

    public boolean getFlag(byte x, byte y, byte z) {
        int index = getFlagIndex(x, z);
        int bit = getBitOfFlag(y);

        return (generateByFlag.flags()[index] & bit) > 0;
    }

    public int getBitOfFlag(byte y) {
        return 1 << (y + 15);
    }

    public int getFlagIndex(byte x, byte z) {
        return (x + 15) * 31 + (z + 15);
    }

    @Override
    public @NotNull PBEffectType<? extends PBEffect> type() {
        return PBEffectInit.GEN_FLAGS;
    }
}
