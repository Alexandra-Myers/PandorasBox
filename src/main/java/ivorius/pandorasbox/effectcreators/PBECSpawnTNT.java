/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnTNT(IValue time, IValue number, IValue fuseTime, ZValue spawnFromEffectCenter, ValueSpawn valueSpawn, ValueThrow valueThrow) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnTNT> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECSpawnTNT::time),
                            IValue.CODEC.fieldOf("number").forGetter(PBECSpawnTNT::number),
                            IValue.CODEC.fieldOf("fuse_time").forGetter(PBECSpawnTNT::fuseTime),
                            ZValue.CODEC.fieldOf("spawn_from_effect_center").forGetter(PBECSpawnTNT::spawnFromEffectCenter),
                            ValueSpawn.CODEC.fieldOf("value_spawn").forGetter(PBECSpawnTNT::valueSpawn),
                            ValueThrow.CODEC.fieldOf("value_throw").forGetter(PBECSpawnTNT::valueThrow))
                    .apply(instance, PBECSpawnTNT::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);

        String[][] entitiesToSpawn = new String[number][];
        for (int i = 0; i < number; i++) {
            entitiesToSpawn[i] = new String[]{"pbspecial_tnt" + this.fuseTime.getValue(random)};
        }

        return PBECSpawnEntities.constructEffect(random, entitiesToSpawn, time, spawnFromEffectCenter, valueThrow, valueSpawn);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
