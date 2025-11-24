/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Created by lukas on 31.03.14.
 */
public record WeightedStructure(double weight, Holder<Structure> structure) implements WeightedSelector.Item {
    public static final Codec<WeightedStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedStructure::weight),
                        Structure.CODEC.fieldOf("structure").forGetter(WeightedStructure::structure))
                    .apply(instance, WeightedStructure::new));
}