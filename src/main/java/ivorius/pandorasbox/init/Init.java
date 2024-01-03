package ivorius.pandorasbox.init;

import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effectholder.FixedChanceEffectHolder;
import ivorius.pandorasbox.effectholder.PositiveOrNegativeEffectHolder;
import ivorius.pandorasbox.effects.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class Init {
    public static final ResourceKey<Registry<EffectHolder>> EFFECT_HOLDER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation("pandorasbox:pandora_effect_holders"));
    public static final Registry<EffectHolder> EFFECT_HOLDER_REGISTRY = FabricRegistryBuilder.createDefaulted(EFFECT_HOLDER_REGISTRY_KEY, new ResourceLocation("matryoshka")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static final ResourceKey<Registry<Class<? extends PBEffect>>> BOX_EFFECT_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation("pandorasbox:pandora_effects"));
    public static final Registry<Class<? extends PBEffect>> BOX_EFFECT_REGISTRY = FabricRegistryBuilder.createDefaulted(BOX_EFFECT_REGISTRY_KEY, new ResourceLocation("duplicate_box")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static FixedChanceEffectHolder makeFixedChance(String name, double fixedChance) {
        return Registry.register(EFFECT_HOLDER_REGISTRY, ResourceKey.create(EFFECT_HOLDER_REGISTRY.key(), new ResourceLocation(name)), new FixedChanceEffectHolder(fixedChance));
    }
    public static PositiveOrNegativeEffectHolder makePositiveOrNegative(String name, boolean good) {
        return Registry.register(EFFECT_HOLDER_REGISTRY, ResourceKey.create(EFFECT_HOLDER_REGISTRY.key(), new ResourceLocation(name)), new PositiveOrNegativeEffectHolder(good));
    }
    public static Class<? extends PBEffect> registerBoxEffect(Class<? extends PBEffect> clazz, String name) {
        return Registry.register(BOX_EFFECT_REGISTRY, ResourceKey.create(BOX_EFFECT_REGISTRY.key(), new ResourceLocation(name)), clazz);
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
