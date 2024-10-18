package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effectholder.FixedChanceEffectHolder;
import ivorius.pandorasbox.effectholder.FixedChancePositiveOrNegativeEffectHolder;
import ivorius.pandorasbox.effectholder.PositiveOrNegativeEffectHolder;
import ivorius.pandorasbox.effects.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class Init {
    public static final ResourceKey<Registry<EffectHolder>> EFFECT_HOLDER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effect_holders"));
    public static final Registry<EffectHolder> EFFECT_HOLDER_REGISTRY = FabricRegistryBuilder.createDefaulted(EFFECT_HOLDER_REGISTRY_KEY, ResourceLocation.withDefaultNamespace("matryoshka")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static final ResourceKey<Registry<Class<? extends PBEffect>>> BOX_EFFECT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effects"));
    public static final Registry<Class<? extends PBEffect>> BOX_EFFECT_REGISTRY = FabricRegistryBuilder.createDefaulted(BOX_EFFECT_REGISTRY_KEY, ResourceLocation.withDefaultNamespace("duplicate_box")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static FixedChanceEffectHolder makeFixedChance(String name, double fixedChance) {
        return Registry.register(EFFECT_HOLDER_REGISTRY, ResourceKey.create(EFFECT_HOLDER_REGISTRY.key(), ResourceLocation.withDefaultNamespace(name)), new FixedChanceEffectHolder(fixedChance));
    }
    public static FixedChancePositiveOrNegativeEffectHolder makePositiveOrNegativeFixedChance(String name, double fixedChance, boolean good) {
        return Registry.register(EFFECT_HOLDER_REGISTRY, ResourceKey.create(EFFECT_HOLDER_REGISTRY.key(), ResourceLocation.withDefaultNamespace(name)), new FixedChancePositiveOrNegativeEffectHolder(fixedChance, good));
    }
    public static PositiveOrNegativeEffectHolder makePositiveOrNegative(String name, boolean good) {
        return Registry.register(EFFECT_HOLDER_REGISTRY, ResourceKey.create(EFFECT_HOLDER_REGISTRY.key(), ResourceLocation.withDefaultNamespace(name)), new PositiveOrNegativeEffectHolder(good));
    }
    public static Class<? extends PBEffect> registerBoxEffect(Class<? extends PBEffect> clazz, String name) {
        return Registry.register(BOX_EFFECT_REGISTRY, ResourceKey.create(BOX_EFFECT_REGISTRY.key(), ResourceLocation.withDefaultNamespace(name)), clazz);
    }

    public static void init() {
        DataSerializerInit.registerDataSerializers();
        FeatureInit.registerFeatures();
        BlockInit.registerBlocks();
        ItemInit.registerItems();
        BlockEntityInit.registerBlockEntities();
        EntityInit.registerEntities();
        ArgumentInit.registerArguments();
        PBEffectInit.registerPandora();
    }

}
