/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.utils.EquipmentSet;

/**
 * Created by lukas on 31.03.14.
 */
public record WeightedSet(double weight, EquipmentSet equipmentSet) implements WeightedSelector.Item {
    public static final Codec<WeightedSet> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.DOUBLE.fieldOf("weight").forGetter(WeightedSet::weight),
                            EquipmentSet.INDIRECT_CODEC.fieldOf("set").forGetter(WeightedSet::equipmentSet))
                    .apply(instance, WeightedSet::new));
}
