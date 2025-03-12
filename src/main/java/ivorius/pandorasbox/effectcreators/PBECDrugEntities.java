/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntityBased;
import ivorius.pandorasbox.effects.entity.DrugEntityEffect;
import ivorius.pandorasbox.mods.PsychedelicraftHooks;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.psychedelicraft.entity.drug.influence.DrugInfluence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECDrugEntities(IValue time, IValue number, DValue range, float chanceForMoreEffects, List<PsychedelicraftHooks.WeightedDrugType> drugTypes, Optional<Vector3f> color) implements PBEffectCreator {
    public static final MapCodec<PBECDrugEntities> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECDrugEntities::time),
                            IValue.CODEC.fieldOf("number").forGetter(PBECDrugEntities::number),
                            DValue.CODEC.fieldOf("range").forGetter(PBECDrugEntities::range),
                            Codec.floatRange(0, 1).fieldOf("chance_for_more_effects").forGetter(PBECDrugEntities::chanceForMoreEffects),
                            PsychedelicraftHooks.WeightedDrugType.DRUG_TYPE_CODEC.listOf().fieldOf("drug_types").forGetter(PBECDrugEntities::drugTypes),
                            RecordCodecBuilder.<Vector3f>create((i) -> i.group(Codec.FLOAT.fieldOf("r").forGetter(Vector3f::x), Codec.FLOAT.fieldOf("g").forGetter(Vector3f::y), Codec.FLOAT.fieldOf("b").forGetter(Vector3f::z)).apply(i, Vector3f::new))
                                    .optionalFieldOf("color").forGetter(PBECDrugEntities::color))
                    .apply(instance, PBECDrugEntities::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int number = this.number.getValue(random);
        int time = this.time.getValue(random);
        double range = this.range.getValue(random);

        List<DrugInfluence> effects = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            PsychedelicraftHooks.WeightedDrugType weightedDrug = WeightedSelector.selectItem(random, drugTypes);
            float value = random.nextFloat() * (weightedDrug.maxAddValue() - weightedDrug.minAddValue()) + weightedDrug.minAddValue();
            DrugInfluence drugInfluence = color.map(vector3f -> new DrugInfluence(weightedDrug.drugTypeHolder().value(), 0, 0, 0, value, vector3f)).orElseGet(() -> new DrugInfluence(weightedDrug.drugTypeHolder().value(), 0, 0, 0, value));

            effects.add(drugInfluence);
        }

        return new PBEffectEntityBased(time, range, new DrugEntityEffect(effects));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return chanceForMoreEffects;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}