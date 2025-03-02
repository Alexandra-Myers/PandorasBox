package ivorius.pandorasbox.init;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.*;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class Init {
    public static final ResourceKey<Registry<EffectHolder>> EFFECT_HOLDER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effect_holders"));
    public static final ResourceKey<Registry<Class<? extends PBEffect>>> BOX_EFFECT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effects"));
    public static final Registry<Class<? extends PBEffect>> BOX_EFFECT_REGISTRY = FabricRegistryBuilder.createDefaulted(BOX_EFFECT_REGISTRY_KEY, ResourceLocation.withDefaultNamespace("duplicate_box")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static final ResourceKey<Registry<MapCodec<? extends PBEffectCreator>>> BOX_EFFECT_CREATOR_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effect_creator_types"));
    public static final Registry<MapCodec<? extends PBEffectCreator>> BOX_EFFECT_CREATOR_REGISTRY = FabricRegistryBuilder.createDefaulted(BOX_EFFECT_CREATOR_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "duplicate_box")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static Class<? extends PBEffect> registerBoxEffect(Class<? extends PBEffect> clazz, String name) {
        return Registry.register(BOX_EFFECT_REGISTRY, ResourceKey.create(BOX_EFFECT_REGISTRY.key(), ResourceLocation.withDefaultNamespace(name)), clazz);
    }
    public static MapCodec<? extends PBEffectCreator> registerBoxEffectCreatorType(MapCodec<? extends PBEffectCreator> mapCodec, String name) {
        return Registry.register(BOX_EFFECT_CREATOR_REGISTRY, ResourceKey.create(BOX_EFFECT_CREATOR_REGISTRY.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }

    public static void init() {
        DataSerializerInit.registerDataSerializers();
        FeatureInit.registerFeatures();
        BlockInit.registerBlocks();
        ItemInit.registerItems();
        BlockEntityInit.registerBlockEntities();
        EntityInit.registerEntities();
        PBEffectInit.registerPandora();
        DynamicRegistries.registerSynced(EFFECT_HOLDER_REGISTRY_KEY, EffectHolder.CODEC);
    }

}
