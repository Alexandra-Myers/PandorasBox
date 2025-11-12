package ivorius.pandorasbox.mods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBECDrugEntities;
import ivorius.pandorasbox.effects.entity.DrugEntityEffect;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.psychedelicraft.entity.drug.DrugProperties;
import ivorius.psychedelicraft.entity.drug.DrugType;
import ivorius.psychedelicraft.entity.drug.influence.DrugInfluence;
import ivorius.psychedelicraft.entity.drug.influence.DrugInfluenceInstance;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

import static ivorius.pandorasbox.init.Init.registerEntityEffectType;

public class PsychedelicraftHooks {
    public static void register() {
        registerEntityEffectType(DrugEntityEffect.CODEC, "drug_entities");
        Init.registerBoxEffectCreatorType(PBECDrugEntities.CODEC, "drug_entities");
    }
    public static void addDrugValue(LivingEntity livingEntity, DrugInfluence drug, float drugStrength) {
        Optional<DrugProperties> properties = DrugProperties.of(livingEntity);
        properties.ifPresent(drugProperties -> drugProperties.addToDrug(drug.drugType(), drugStrength, new DrugInfluenceInstance(drug)));
    }
    public record WeightedDrugType(double weight, float minAddValue, float maxAddValue, Holder<DrugType<?>> drugTypeHolder) implements WeightedSelector.Item {
        public static final Codec<WeightedDrugType> DRUG_TYPE_CODEC = RecordCodecBuilder.<WeightedDrugType>create(instance ->
                instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedDrugType::weight),
                                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("min_add_value").forGetter(WeightedDrugType::minAddValue),
                                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("max_add_value").forGetter(WeightedDrugType::maxAddValue),
                                DrugType.REGISTRY.holderByNameCodec().fieldOf("drug_type").forGetter(WeightedDrugType::drugTypeHolder))
                        .apply(instance, WeightedDrugType::new)).validate(weightedDrugType -> {
                            if (weightedDrugType.minAddValue > weightedDrugType.maxAddValue) return DataResult.error(() -> "Expected drug's minimum add value to be less than or equal to its maximum!");
                            else return DataResult.success(weightedDrugType);
        });
    }
}
