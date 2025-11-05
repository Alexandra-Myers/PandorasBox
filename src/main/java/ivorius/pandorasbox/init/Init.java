package ivorius.pandorasbox.init;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.*;
import ivorius.pandorasbox.effects.entity.EntityEffect;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.effects.generate.flags.GenerateByFlag;
import ivorius.pandorasbox.effects.generate.two_dimensional.Generate2D;
import ivorius.pandorasbox.effects.position.PositionEffect;
import ivorius.pandorasbox.effects.spawn_entities.SpawnEntitiesEffect;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public class Init {
    public static final ResourceKey<Registry<EffectHolder>> EFFECT_HOLDER_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effect_holders"));

    public static final ResourceKey<Registry<MapCodec<? extends PBEffect>>> BOX_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effects"));
    public static final Registry<MapCodec<? extends PBEffect>> BOX_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(BOX_EFFECT_TYPE_REGISTRY_KEY, Identifier.withDefaultNamespace("duplicate_box")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends EntityEffect>>> ENTITY_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "entity_based_effects"));
    public static final Registry<MapCodec<? extends EntityEffect>> ENTITY_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(ENTITY_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "bomberman")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends PositionEffect>>> POSITION_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "position_based_effects"));
    public static final Registry<MapCodec<? extends PositionEffect>> POSITION_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(POSITION_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "random_explosions")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends SpawnEntitiesEffect>>> SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "spawn_entities_based_effects"));
    public static final Registry<MapCodec<? extends SpawnEntitiesEffect>> SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "spawn_entities_id_list")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends GenerateByFlag>>> GEN_FLAGS_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "gen_by_flag_effects"));
    public static final Registry<MapCodec<? extends GenerateByFlag>> GEN_FLAGS_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(GEN_FLAGS_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "gen_cover")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends Generate2D>>> GEN_2D_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "gen_two_dimensional_effects"));
    public static final Registry<MapCodec<? extends Generate2D>> GEN_2D_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(GEN_2D_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "gen_dome")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends GenerateEffect>>> GENERATE_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "generate_effects"));
    public static final Registry<MapCodec<? extends GenerateEffect>> GENERATE_EFFECT_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(GENERATE_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "gen_convert")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends BlockMapper>>> BLOCK_MAPPER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "modify_blocks"));
    public static final Registry<MapCodec<? extends BlockMapper>> BLOCK_MAPPER_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(BLOCK_MAPPER_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "convert_simple")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends EntitySpawner>>> ENTITY_SPAWNER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "spawn_entities_convert"));
    public static final Registry<MapCodec<? extends EntitySpawner>> ENTITY_SPAWNER_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(ENTITY_SPAWNER_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "spawn_random")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends FeatureGenerator>>> FEATURE_GENERATOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "feature_generators_convert"));
    public static final Registry<MapCodec<? extends FeatureGenerator>> FEATURE_GENERATOR_TYPE_REGISTRY = FabricRegistryBuilder.createDefaulted(FEATURE_GENERATOR_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "generate_feature")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ResourceKey<Registry<MapCodec<? extends PBEffectCreator>>> BOX_EFFECT_CREATOR_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effect_creator_types"));
    public static final Registry<MapCodec<? extends PBEffectCreator>> BOX_EFFECT_CREATOR_REGISTRY = FabricRegistryBuilder.createDefaulted(BOX_EFFECT_CREATOR_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "duplicate_box")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static MapCodec<? extends PBEffect> registerBoxEffectType(MapCodec<? extends PBEffect> mapCodec, String name) {
        return Registry.register(BOX_EFFECT_TYPE_REGISTRY, ResourceKey.create(BOX_EFFECT_TYPE_REGISTRY_KEY, Identifier.withDefaultNamespace(name)), mapCodec);
    }
    public static MapCodec<? extends EntityEffect> registerEntityEffectType(MapCodec<? extends EntityEffect> mapCodec, String name) {
        return Registry.register(ENTITY_EFFECT_TYPE_REGISTRY, ResourceKey.create(ENTITY_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends PositionEffect> registerPositionEffectType(MapCodec<? extends PositionEffect> mapCodec, String name) {
        return Registry.register(POSITION_EFFECT_TYPE_REGISTRY, ResourceKey.create(POSITION_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends SpawnEntitiesEffect> registerSpawnEntitiesEffectType(MapCodec<? extends SpawnEntitiesEffect> mapCodec, String name) {
        return Registry.register(SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY, ResourceKey.create(SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends GenerateByFlag> registerGenFlagsEffectType(MapCodec<? extends GenerateByFlag> mapCodec, String name) {
        return Registry.register(GEN_FLAGS_EFFECT_TYPE_REGISTRY, ResourceKey.create(GEN_FLAGS_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends Generate2D> registerGenTwoDimensionalEffectType(MapCodec<? extends Generate2D> mapCodec, String name) {
        return Registry.register(GEN_2D_EFFECT_TYPE_REGISTRY, ResourceKey.create(GEN_2D_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends GenerateEffect> registerGenerateEffectType(MapCodec<? extends GenerateEffect> mapCodec, String name) {
        return Registry.register(GENERATE_EFFECT_TYPE_REGISTRY, ResourceKey.create(GENERATE_EFFECT_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends BlockMapper> registerBlockMapperType(MapCodec<? extends BlockMapper> mapCodec, String name) {
        return Registry.register(BLOCK_MAPPER_TYPE_REGISTRY, ResourceKey.create(BLOCK_MAPPER_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends EntitySpawner> registerEntitySpawnerType(MapCodec<? extends EntitySpawner> mapCodec, String name) {
        return Registry.register(ENTITY_SPAWNER_TYPE_REGISTRY, ResourceKey.create(ENTITY_SPAWNER_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends FeatureGenerator> registerFeatureGeneratorType(MapCodec<? extends FeatureGenerator> mapCodec, String name) {
        return Registry.register(FEATURE_GENERATOR_TYPE_REGISTRY, ResourceKey.create(FEATURE_GENERATOR_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }
    public static MapCodec<? extends PBEffectCreator> registerBoxEffectCreatorType(MapCodec<? extends PBEffectCreator> mapCodec, String name) {
        return Registry.register(BOX_EFFECT_CREATOR_REGISTRY, ResourceKey.create(BOX_EFFECT_CREATOR_REGISTRY_KEY, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), mapCodec);
    }

    public static void init() {
        DataSerializerInit.registerDataSerializers();
        FeatureInit.registerFeatures();
        ComponentInit.registerComponents();
        BlockInit.registerBlocks();
        ItemInit.registerItems();
        BlockEntityInit.registerBlockEntities();
        EntityInit.registerEntities();
        MobEffectInit.registerEffects();
        PBEffectInit.registerPandora();
        DynamicRegistries.registerSynced(EFFECT_HOLDER_REGISTRY_KEY, EffectHolder.DIRECT_CODEC);
    }

}
