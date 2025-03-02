/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenTargets;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECTargets(IValue time, DValue range, DValue targetSize, DValue entityDensity, List<WeightedEntity> entities) implements PBEffectCreator {
    public static final MapCodec<PBECTargets> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECTargets::time),
                            DValue.CODEC.fieldOf("range").forGetter(PBECTargets::range),
                            DValue.CODEC.fieldOf("target_size").forGetter(PBECTargets::targetSize),
                            DValue.CODEC.fieldOf("entity_density").forGetter(PBECTargets::entityDensity),
                            WeightedEntity.CODEC.listOf().fieldOf("entities").forGetter(PBECTargets::entities))
                    .apply(instance, PBECTargets::new));
    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        double targetSize = this.targetSize.getValue(random);
        double entityDensity = this.entityDensity.getValue(random);
        int time = this.time.getValue(random);

        WeightedEntity entity = PandorasBoxHelper.getRandomEntityFromList(random, entities);

        PBEffectGenTargets gen = new PBEffectGenTargets(time, entity.entityID(), range, targetSize, entityDensity);
        gen.createTargets(world, x, y, z, random);
        return gen;
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
