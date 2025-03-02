/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.utils.WeightedWithRandomCount;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

/**
 * Created by lukas on 31.03.14.
 */
public record WeightedEntity(String entityID, WeightedWithRandomCount count) implements WeightedSelector.Item {
    public static final String[] PB_SPECIAL_LOCS = new String[]{
            "pbspecial_angry_wolf",
            "pbspecial_charged_creeper",
            "pbspecial_fireworks",
            "pbspecial_tnt",
            "pbspecial_invisible_tnt",
            "pbspecial_parrot_tamed",
            "pbspecial_cat_tamed",
            "pbspecial_wolf_tamed",
            "pbspecial_experience"
    };
    public static final Codec<WeightedEntity> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ResourceLocation.CODEC.validate(resourceLocation -> BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation) || Arrays.asList(PB_SPECIAL_LOCS).contains(resourceLocation.getPath()) ? DataResult.success(resourceLocation) : DataResult.error(() -> "Unknown registry key in " + Registries.ENTITY_TYPE + "found, and was not a pbspecial entity! Input: " + resourceLocation)).xmap(ResourceLocation::toString, ResourceLocation::tryParse).fieldOf("entity").forGetter(WeightedEntity::entityID),
                            WeightedWithRandomCount.CODEC_FORCE.forGetter(WeightedEntity::count))
                    .apply(instance, WeightedEntity::new));
    public static final Codec<WeightedEntity> NO_SPECIAL_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ResourceLocation.CODEC.validate(resourceLocation -> BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation) ? DataResult.success(resourceLocation) : DataResult.error(() -> "Unknown registry key in " + Registries.ENTITY_TYPE + ": " + resourceLocation)).xmap(ResourceLocation::toString, ResourceLocation::tryParse).fieldOf("entity").forGetter(WeightedEntity::entityID),
                            WeightedWithRandomCount.CODEC_FORCE.forGetter(WeightedEntity::count))
                    .apply(instance, WeightedEntity::new));
    public WeightedEntity(double weight, String entityID, int minNumber, int maxNumber) {
        this(entityID, new WeightedWithRandomCount(minNumber, maxNumber, weight));
    }

    @Override
    public double weight()
    {
        return count.weight();
    }
}
