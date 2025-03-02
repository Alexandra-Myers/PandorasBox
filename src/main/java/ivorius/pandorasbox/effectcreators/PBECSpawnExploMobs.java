/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnExploMobs(IValue time, IValue number, IValue fuseTime, IValue nameEntities, ZValue spawnFromEffectCenter, List<WeightedEntity> entityIDs, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnExploMobs> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECSpawnExploMobs::time),
                            IValue.CODEC.fieldOf("number").forGetter(PBECSpawnExploMobs::number),
                            IValue.CODEC.fieldOf("fuse_time").forGetter(PBECSpawnExploMobs::fuseTime),
                            IValue.CODEC.optionalFieldOf("named_entities", new IConstant(0)).forGetter(PBECSpawnExploMobs::nameEntities),
                            ZValue.CODEC.fieldOf("spawn_from_effect_center").forGetter(PBECSpawnExploMobs::spawnFromEffectCenter),
                            WeightedEntity.CODEC.listOf().fieldOf("entities").forGetter(PBECSpawnExploMobs::entityIDs),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnExploMobs::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnExploMobs::valueSpawn))
                    .apply(instance, PBECSpawnExploMobs::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);
        WeightedEntity entity = PandorasBoxHelper.getRandomEntityFromList(random, entityIDs);
        boolean invisible = random.nextBoolean();

        String[][] entitiesToSpawn = new String[number][];
        for (int i = 0; i < number; i++)
        {
            entitiesToSpawn[i] = new String[2];
            entitiesToSpawn[i][0] = (invisible ? "pbspecial_invisible_tnt" : "pbspecial_tnt") + this.fuseTime.getValue(random);
            entitiesToSpawn[i][1] = entity.entityID();
        }

        int nameEntities = this.nameEntities.getValue(random);

        return PBECSpawnEntities.constructEffect(random, entitiesToSpawn, time, nameEntities, 0, 0, spawnFromEffectCenter, valueThrow.orElse(null), valueSpawn.orElse(null));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
