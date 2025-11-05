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
import net.minecraft.resources.Identifier;

import java.util.Arrays;

/**
 * Created by lukas on 31.03.14.
 */
public record WeightedEntity(String entityID, WeightedWithRandomCount count) implements WeightedSelector.Item {
    public static final String[] PB_SPECIAL_LOCS = new String[] {
            "pbspecial_colorful_sheep",
            "pbspecial_hogfather",
            "pbspecial_angry_wolf",
            "pbspecial_charged_creeper",
            "pbspecial_zombie_horseman",
            "pbspecial_nautilus_jockey",
            "pbspecial_fireworks",
            "pbspecial_tnt",
            "pbspecial_invisible_tnt",
            "pbspecial_parrot_tamed",
            "pbspecial_cat_tamed",
            "pbspecial_wolf_tamed",
            "pbspecial_experience"
    };
    public static final Codec<String> ID_CODEC = Identifier.CODEC.validate(identifier -> BuiltInRegistries.ENTITY_TYPE.containsKey(identifier) || Arrays.asList(PB_SPECIAL_LOCS).contains(identifier.getPath()) ? DataResult.success(identifier) : DataResult.error(() -> "Unknown registry key in " + Registries.ENTITY_TYPE + " found, and was not a pbspecial entity! Input: " + identifier)).xmap(Identifier::toString, Identifier::tryParse);
    public static final Codec<WeightedEntity> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ID_CODEC.fieldOf("entity").forGetter(WeightedEntity::entityID),
                            WeightedWithRandomCount.CODEC_FORCE.forGetter(WeightedEntity::count))
                    .apply(instance, WeightedEntity::new));
    public static final Codec<WeightedEntity> NO_SPECIAL_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Identifier.CODEC.validate(identifier -> BuiltInRegistries.ENTITY_TYPE.containsKey(identifier) ? DataResult.success(identifier) : DataResult.error(() -> "Unknown registry key in " + Registries.ENTITY_TYPE + ": " + identifier)).xmap(Identifier::toString, Identifier::tryParse).fieldOf("entity").forGetter(WeightedEntity::entityID),
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
