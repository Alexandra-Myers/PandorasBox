/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSpawnEntities;
import ivorius.pandorasbox.effects.PBEffectSpawnEntityIDList;
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
public record PBECSpawnEntities(IValue time, IValue number, IValue entitiesPerTower, IValue nameEntities, IValue equipLevel, IValue buffLevel, ZValue spawnFromEffectCenter, List<WeightedEntity> entityIDs, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnEntities> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECSpawnEntities::time),
                            IValue.CODEC.fieldOf("number").forGetter(PBECSpawnEntities::number),
                            IValue.CODEC.optionalFieldOf("entities_per_tower", new IConstant(1)).forGetter(PBECSpawnEntities::entitiesPerTower),
                            IValue.CODEC.optionalFieldOf("named_entities", new IConstant(0)).forGetter(PBECSpawnEntities::nameEntities),
                            IValue.CODEC.optionalFieldOf("equipment_level", new IConstant(0)).forGetter(PBECSpawnEntities::equipLevel),
                            IValue.CODEC.optionalFieldOf("buff_level", new IConstant(0)).forGetter(PBECSpawnEntities::buffLevel),
                            ZValue.CODEC.fieldOf("spawn_from_effect_center").forGetter(PBECSpawnEntities::spawnFromEffectCenter),
                            WeightedEntity.CODEC.listOf().fieldOf("entities").forGetter(PBECSpawnEntities::entityIDs),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnEntities::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnEntities::valueSpawn))
                    .apply(instance, PBECSpawnEntities::new));
    public PBECSpawnEntities(IValue time, IValue number, IValue entitiesPerTower, IValue equipLevel, IValue buffLevel, IValue nameEntities, ZValue spawnFromEffectCenter, List<WeightedEntity> entityIDs) {
        this(time, number, entitiesPerTower, nameEntities, equipLevel, buffLevel, spawnFromEffectCenter, entityIDs, Optional.of(defaultThrow()), Optional.of(defaultSpawn()));
    }

    public static ValueThrow defaultThrow()
    {
        return new ValueThrow(new DLinear(0.1, 0.4), new DLinear(0.2, 1.0));
    }

    public static ValueSpawn defaultSpawn()
    {
        return new ValueSpawn(new DLinear(8.0, 30.0), new DConstant(0.0));
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);
        int[] towerSize = ValueHelper.getValueRange(entitiesPerTower, random);

        int nameEntities = this.nameEntities.getValue(random);
        int equipLevel = this.equipLevel.getValue(random);
        int buffLevel = this.buffLevel.getValue(random);

        WeightedEntity[] entitySelection = PandorasBoxHelper.getRandomEntityList(random, entityIDs);

        String[][] entitiesToSpawn = new String[number][];
        for (int i = 0; i < number; i++) {
            entitiesToSpawn[i] = new String[towerSize[0] + random.nextInt(towerSize[1] - towerSize[0] + 1)];

            for (int j = 0; j < entitiesToSpawn[i].length; j++) {
                entitiesToSpawn[i][j] = entitySelection[random.nextInt(entitySelection.length)].entityID();
            }
        }

        return constructEffect(random, entitiesToSpawn, time, nameEntities, equipLevel, buffLevel, spawnFromEffectCenter, valueThrow.orElse(null), valueSpawn.orElse(null));
    }

    public static PBEffect constructEffect(RandomSource random, String[][] entitiesToSpawn, int time, ZValue spawnFromEffectCenter, ValueThrow valueThrow, ValueSpawn valueSpawn) {
        return constructEffect(random, entitiesToSpawn, time, 0, 0, 0, spawnFromEffectCenter, valueThrow, valueSpawn);
    }

    public static PBEffect constructEffect(RandomSource random, String[][] entitiesToSpawn, int time, int nameEntities, int equipLevel, int buffLevel, ZValue spawnFromEffectCenter, ValueThrow valueThrow, ValueSpawn valueSpawn) {
        boolean canSpawn = valueSpawn != null;
        boolean canThrow = valueThrow != null;

        if (canThrow && (!canSpawn || random.nextBoolean())) {
            PBEffectSpawnEntityIDList effect = new PBEffectSpawnEntityIDList(time, entitiesToSpawn, nameEntities, equipLevel, buffLevel);
            effect.setSpawnsFromBox(!spawnFromEffectCenter.getValue(random));
            setEffectThrow(effect, random, valueThrow);
            return effect;
        } else if (canSpawn) {
            PBEffectSpawnEntityIDList effect = new PBEffectSpawnEntityIDList(time, entitiesToSpawn, nameEntities, equipLevel, buffLevel);
            effect.setSpawnsFromBox(!spawnFromEffectCenter.getValue(random));
            setEffectSpawn(effect, random, valueSpawn);
            return effect;
        }

        throw new RuntimeException("Both spawnRange and throwStrength are null!");
    }

    public static void setEffectThrow(PBEffectSpawnEntities effect, RandomSource random, ValueThrow valueThrow) {
        double[] throwX = ValueHelper.getValueRange(valueThrow.throwStrengthSide(), random);
        double[] throwY = ValueHelper.getValueRange(valueThrow.throwStrengthY(), random);
        effect.setDoesSpawnDirect(throwX[0], throwX[1], throwY[0], throwY[1]);
    }

    public static void setEffectSpawn(PBEffectSpawnEntities effect, RandomSource random, ValueSpawn valueSpawn) {
        double range = valueSpawn.spawnRange().getValue(random);
        double spawnShift = valueSpawn.spawnShift().getValue(random);
        effect.setDoesNotSpawnDirect(range, spawnShift);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
