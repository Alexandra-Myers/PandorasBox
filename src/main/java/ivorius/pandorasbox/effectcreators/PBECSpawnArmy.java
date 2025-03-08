/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnArmy(IValue groups, IValue equipLevel, ZValue spawnFromEffectCenter, List<WeightedEntity> entityIDs, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnArmy> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("groups").forGetter(PBECSpawnArmy::groups),
                            IValue.CODEC.optionalFieldOf("equipment_level", new IConstant(0)).forGetter(PBECSpawnArmy::equipLevel),
                            ZValue.CODEC.fieldOf("spawn_from_effect_center").forGetter(PBECSpawnArmy::spawnFromEffectCenter),
                            WeightedEntity.CODEC.listOf().fieldOf("entities").forGetter(PBECSpawnArmy::entityIDs),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnArmy::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnArmy::valueSpawn))
                    .apply(instance, PBECSpawnArmy::new));

    public PBECSpawnArmy(IValue groups, IValue equipLevel, ZValue spawnFromEffectCenter, List<WeightedEntity> entityIDs) {
        this(groups, equipLevel, spawnFromEffectCenter, entityIDs, Optional.of(PBECSpawnEntities.defaultThrow()), Optional.of(PBECSpawnEntities.defaultSpawn()));
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int groups = this.groups.getValue(random);

        WeightedEntity[] entitySelection = PandorasBoxHelper.getRandomEntityList(random, entityIDs);

        PBEffect[] effects = new PBEffect[groups * 2];
        int[] delays = new int[effects.length];

        for (int i = 0; i < groups; i++) {
            WeightedEntity soldierType = entitySelection[random.nextInt(entitySelection.length)];
            String[][] soldiers = new String[new ILinear(soldierType.count().min(), soldierType.count().max().orElse(soldierType.count().min())).getValue(random)][];
            Arrays.fill(soldiers, new String[]{soldierType.entityID()});

            int equipLevel = this.equipLevel.getValue(random);
            int equipLevelCaptain = 5 + equipLevel;
            int buffLevelCaptain = random.nextInt(5);

            effects[i * 2] = PBECSpawnEntities.constructEffect(random, soldiers, 50, 0, equipLevel, 0, spawnFromEffectCenter, valueThrow.orElse(null), valueSpawn.orElse(null));
            effects[i * 2 + 1] = PBECSpawnEntities.constructEffect(random, new String[][]{new String[]{soldierType.entityID()}}, 25, 1, equipLevelCaptain, buffLevelCaptain, spawnFromEffectCenter, valueThrow.orElse(null), valueSpawn.orElse(null));
            delays[i * 2] = i * 50;
            delays[i * 2 + 1] = i * 50 + 25;
        }

        return new PBEffectMulti(effects, delays);
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
