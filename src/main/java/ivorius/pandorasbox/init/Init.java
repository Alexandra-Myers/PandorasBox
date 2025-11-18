package ivorius.pandorasbox.init;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import ivorius.pandorasbox.effectcreators.generate.GenerateEffectCreator;
import ivorius.pandorasbox.effectcreators.generate.block_mappers.BlockMapperCreator;
import ivorius.pandorasbox.effectcreators.generate.feature_generators.FeatureGeneratorCreator;
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
import ivorius.pandorasbox.utils.EquipmentSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class Init {
    public static final ResourceKey<Registry<EquipmentSet>> EQUIPMENT_SET_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "equipment_sets"));
    public static final ResourceKey<Registry<EffectHolder>> EFFECT_HOLDER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "pandora_effect_holders"));
    public static final ResourceKey<Registry<EffectHolder>> MELTDOWN_EFFECT_HOLDER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "meltdown_effect_holders"));

    public static final ResourceKey<Registry<MapCodec<? extends PBEffect>>> BOX_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "pandora_effects"));
    public static final DeferredRegister<MapCodec<? extends PBEffect>> BOX_EFFECT_TYPES = DeferredRegister.create(BOX_EFFECT_TYPE_REGISTRY_KEY, "minecraft");
    public static final Supplier<IForgeRegistry<MapCodec<? extends PBEffect>>> BOX_EFFECT_TYPE_REGISTRY = BOX_EFFECT_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation("duplicate_box")));

    public static final ResourceKey<Registry<MapCodec<? extends EntityEffect>>> ENTITY_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "entity_based_effects"));
    public static final DeferredRegister<MapCodec<? extends EntityEffect>> ENTITY_EFFECT_TYPES = DeferredRegister.create(ENTITY_EFFECT_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends EntityEffect>>> ENTITY_EFFECT_TYPE_REGISTRY = ENTITY_EFFECT_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "bomberman")));

    public static final ResourceKey<Registry<MapCodec<? extends PositionEffect>>> POSITION_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "position_based_effects"));
    public static final DeferredRegister<MapCodec<? extends PositionEffect>> POSITION_EFFECT_TYPES = DeferredRegister.create(POSITION_EFFECT_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends PositionEffect>>> POSITION_EFFECT_TYPE_REGISTRY = POSITION_EFFECT_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "random_explosions")));

    public static final ResourceKey<Registry<MapCodec<? extends SpawnEntitiesEffect>>> SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "spawn_entities_based_effects"));
    public static final DeferredRegister<MapCodec<? extends SpawnEntitiesEffect>> SPAWN_ENTITIES_EFFECT_TYPES = DeferredRegister.create(SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends SpawnEntitiesEffect>>> SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY = SPAWN_ENTITIES_EFFECT_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "spawn_entities_id_list")));

    public static final ResourceKey<Registry<MapCodec<? extends GenerateByFlag>>> GEN_FLAGS_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "gen_by_flag_effects"));
    public static final DeferredRegister<MapCodec<? extends GenerateByFlag>> GEN_FLAGS_EFFECT_TYPES = DeferredRegister.create(GEN_FLAGS_EFFECT_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends GenerateByFlag>>> GEN_FLAGS_EFFECT_TYPE_REGISTRY = GEN_FLAGS_EFFECT_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "gen_cover")));

    public static final ResourceKey<Registry<MapCodec<? extends Generate2D>>> GEN_2D_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "gen_two_dimensional_effects"));
    public static final DeferredRegister<MapCodec<? extends Generate2D>> GEN_2D_EFFECT_TYPES = DeferredRegister.create(GEN_2D_EFFECT_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends Generate2D>>> GEN_2D_EFFECT_TYPE_REGISTRY = GEN_2D_EFFECT_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "gen_dome")));

    public static final ResourceKey<Registry<MapCodec<? extends GenerateEffect>>> GENERATE_EFFECT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "generate_effects"));
    public static final DeferredRegister<MapCodec<? extends GenerateEffect>> GENERATE_EFFECT_TYPES = DeferredRegister.create(GENERATE_EFFECT_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends GenerateEffect>>> GENERATE_EFFECT_TYPE_REGISTRY = GENERATE_EFFECT_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "gen_convert")));

    public static final ResourceKey<Registry<MapCodec<? extends BlockMapper>>> BLOCK_MAPPER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "modify_blocks"));
    public static final DeferredRegister<MapCodec<? extends BlockMapper>> BLOCK_MAPPER_TYPES = DeferredRegister.create(BLOCK_MAPPER_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends BlockMapper>>> BLOCK_MAPPER_TYPE_REGISTRY = BLOCK_MAPPER_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "convert_simple")));

    public static final ResourceKey<Registry<MapCodec<? extends EntitySpawner>>> ENTITY_SPAWNER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "spawn_entities_convert"));
    public static final DeferredRegister<MapCodec<? extends EntitySpawner>> ENTITY_SPAWNER_TYPES = DeferredRegister.create(ENTITY_SPAWNER_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends EntitySpawner>>> ENTITY_SPAWNER_TYPE_REGISTRY = ENTITY_SPAWNER_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "spawn_random")));

    public static final ResourceKey<Registry<MapCodec<? extends FeatureGenerator>>> FEATURE_GENERATOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "feature_generators_convert"));
    public static final DeferredRegister<MapCodec<? extends FeatureGenerator>> FEATURE_GENERATOR_TYPES = DeferredRegister.create(FEATURE_GENERATOR_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends FeatureGenerator>>> FEATURE_GENERATOR_TYPE_REGISTRY = FEATURE_GENERATOR_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "generate_feature")));

    public static final ResourceKey<Registry<MapCodec<? extends GenerateEffectCreator>>> GENERATE_EFFECT_CREATOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "generate_effects_creators"));
    public static final DeferredRegister<MapCodec<? extends GenerateEffectCreator>> GENERATE_EFFECT_CREATOR_TYPES = DeferredRegister.create(GENERATE_EFFECT_CREATOR_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends GenerateEffectCreator>>> GENERATE_EFFECT_CREATOR_TYPE_REGISTRY = GENERATE_EFFECT_CREATOR_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "prepare_gen_convert")));

    public static final ResourceKey<Registry<MapCodec<? extends BlockMapperCreator>>> BLOCK_MAPPER_CREATOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "modify_blocks_creators"));
    public static final DeferredRegister<MapCodec<? extends BlockMapperCreator>> BLOCK_MAPPER_CREATOR_TYPES = DeferredRegister.create(BLOCK_MAPPER_CREATOR_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends BlockMapperCreator>>> BLOCK_MAPPER_CREATOR_TYPE_REGISTRY = BLOCK_MAPPER_CREATOR_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "direct")));

    public static final ResourceKey<Registry<MapCodec<? extends FeatureGeneratorCreator>>> FEATURE_GENERATOR_CREATOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "feature_generators_convert_creators"));
    public static final DeferredRegister<MapCodec<? extends FeatureGeneratorCreator>> FEATURE_GENERATOR_CREATOR_TYPES = DeferredRegister.create(FEATURE_GENERATOR_CREATOR_TYPE_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends FeatureGeneratorCreator>>> FEATURE_GENERATOR_CREATOR_TYPE_REGISTRY = FEATURE_GENERATOR_CREATOR_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "direct")));

    public static final ResourceKey<Registry<MapCodec<? extends PBEffectCreator>>> BOX_EFFECT_CREATOR_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PandorasBox.MOD_ID, "pandora_effect_creator_types"));
    public static final DeferredRegister<MapCodec<? extends PBEffectCreator>> BOX_EFFECT_CREATOR_TYPES = DeferredRegister.create(BOX_EFFECT_CREATOR_REGISTRY_KEY, PandorasBox.MOD_ID);
    public static final Supplier<IForgeRegistry<MapCodec<? extends PBEffectCreator>>> BOX_EFFECT_CREATOR_REGISTRY = BOX_EFFECT_CREATOR_TYPES.makeRegistry(defaultedRegistry(new ResourceLocation(PandorasBox.MOD_ID, "duplicate_box")));

    public static void registerBoxEffectType(Supplier<MapCodec<? extends PBEffect>> mapCodec, String name) {
        BOX_EFFECT_TYPES.register(name, mapCodec);
    }
    public static void registerEntityEffectType(Supplier<MapCodec<? extends EntityEffect>> mapCodec, String name) {
        ENTITY_EFFECT_TYPES.register(name, mapCodec);
    }
    public static void registerPositionEffectType(Supplier<MapCodec<? extends PositionEffect>> mapCodec, String name) {
        POSITION_EFFECT_TYPES.register(name, mapCodec);
    }
    public static void registerSpawnEntitiesEffectType(Supplier<MapCodec<? extends SpawnEntitiesEffect>> mapCodec, String name) {
        SPAWN_ENTITIES_EFFECT_TYPES.register(name, mapCodec);
    }
    public static void registerGenFlagsEffectType(Supplier<MapCodec<? extends GenerateByFlag>> mapCodec, String name) {
        GEN_FLAGS_EFFECT_TYPES.register(name, mapCodec);
    }
    public static void registerGenTwoDimensionalEffectType(Supplier<MapCodec<? extends Generate2D>> mapCodec, String name) {
        GEN_2D_EFFECT_TYPES.register(name, mapCodec);
    }
    public static void registerGenerateEffectType(Supplier<MapCodec<? extends GenerateEffect>> mapCodec, String name) {
        GENERATE_EFFECT_TYPES.register(name, mapCodec);
    }
    public static void registerBlockMapperType(Supplier<MapCodec<? extends BlockMapper>> mapCodec, String name) {
        BLOCK_MAPPER_TYPES.register(name, mapCodec);
    }
    public static void registerEntitySpawnerType(Supplier<MapCodec<? extends EntitySpawner>> mapCodec, String name) {
        ENTITY_SPAWNER_TYPES.register(name, mapCodec);
    }
    public static void registerFeatureGeneratorType(Supplier<MapCodec<? extends FeatureGenerator>> mapCodec, String name) {
        FEATURE_GENERATOR_TYPES.register(name, mapCodec);
    }
    public static void registerGenerateEffectCreatorType(Supplier<MapCodec<? extends GenerateEffectCreator>> mapCodec, String name) {
        GENERATE_EFFECT_CREATOR_TYPES.register(name, mapCodec);
    }
    public static void registerBlockMapperCreatorType(Supplier<MapCodec<? extends BlockMapperCreator>> mapCodec, String name) {
        BLOCK_MAPPER_CREATOR_TYPES.register(name, mapCodec);
    }
    public static void registerFeatureGeneratorCreatorType(Supplier<MapCodec<? extends FeatureGeneratorCreator>> mapCodec, String name) {
        FEATURE_GENERATOR_CREATOR_TYPES.register(name, mapCodec);
    }
    public static void registerBoxEffectCreatorType(Supplier<MapCodec<? extends PBEffectCreator>> mapCodec, String name) {
        BOX_EFFECT_CREATOR_TYPES.register(name, mapCodec);
    }

    public static void init(IEventBus bus) {
        DataSerializerInit.registerDataSerializers(bus);
        FeatureInit.registerFeatures(bus);
        BlockInit.registerBlocks(bus);
        ItemInit.registerItems(bus);
        BlockEntityInit.registerBlockEntities(bus);
        EntityInit.registerEntities(bus);
        PBEffectInit.registerPandora();
        BOX_EFFECT_TYPES.register(bus);
        ENTITY_EFFECT_TYPES.register(bus);
        POSITION_EFFECT_TYPES.register(bus);
        SPAWN_ENTITIES_EFFECT_TYPES.register(bus);
        GEN_FLAGS_EFFECT_TYPES.register(bus);
        GEN_2D_EFFECT_TYPES.register(bus);
        GENERATE_EFFECT_TYPES.register(bus);
        BLOCK_MAPPER_TYPES.register(bus);
        ENTITY_SPAWNER_TYPES.register(bus);
        FEATURE_GENERATOR_TYPES.register(bus);
        GENERATE_EFFECT_CREATOR_TYPES.register(bus);
        BLOCK_MAPPER_CREATOR_TYPES.register(bus);
        FEATURE_GENERATOR_CREATOR_TYPES.register(bus);
        BOX_EFFECT_CREATOR_TYPES.register(bus);
    }

    public static <T> Supplier<RegistryBuilder<T>> defaultedRegistry(ResourceLocation defaultKey) {
        return () -> RegistryBuilder.<T>of().setDefaultKey(defaultKey);
    }
}
