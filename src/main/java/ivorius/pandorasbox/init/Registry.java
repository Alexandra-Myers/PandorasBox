package ivorius.pandorasbox.init;

import ivorius.pandorasbox.block.PandorasBoxBlock;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effectholder.FixedChanceEffectHolder;
import ivorius.pandorasbox.effectholder.FixedChancePositiveOrNegativeEffectHolder;
import ivorius.pandorasbox.effectholder.PositiveOrNegativeEffectHolder;
import ivorius.pandorasbox.effects.*;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.items.PandorasBoxItem;
import ivorius.pandorasbox.worldgen.*;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class Registry {
    private static final DeferredRegister<EffectHolder> EFFECT_HOLDERS = DeferredRegister.create(EffectHolder.class, "minecraft");

    public static final Supplier<IForgeRegistry<EffectHolder>> EFFECT_HOLDER_REGISTRY =
            EFFECT_HOLDERS.makeRegistry("pandora_effect_holders", () -> new RegistryBuilder<EffectHolder>().setDefaultKey(new ResourceLocation("matryoshka")));
    private static final DeferredRegister<RegisteredPBEffect> BOX_EFFECTS = DeferredRegister.create(RegisteredPBEffect.class, "minecraft");
    public static final Supplier<IForgeRegistry<RegisteredPBEffect>> BOX_EFFECT_REGISTRY =
            BOX_EFFECTS.makeRegistry("pandora_effects", () -> new RegistryBuilder<RegisteredPBEffect>().setDefaultKey(new ResourceLocation("duplicate_box")));
    private static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);
    private static final DeferredRegister<DataSerializerEntry> SERIALIZERS = DeferredRegister.create(ForgeRegistries.DATA_SERIALIZERS, MOD_ID);
    public static RegistryObject<FixedChanceEffectHolder> makeFixedChance(String name, double fixedChance) {
        return EFFECT_HOLDERS.register(name, () -> new FixedChanceEffectHolder(fixedChance));
    }
    public static RegistryObject<FixedChancePositiveOrNegativeEffectHolder> makePositiveOrNegativeFixedChance(String name, double fixedChance, boolean good) {
        return EFFECT_HOLDERS.register(name, () -> new FixedChancePositiveOrNegativeEffectHolder(fixedChance, good));
    }
    public static RegistryObject<PositiveOrNegativeEffectHolder> makePositiveOrNegative(String name, boolean good) {
        return EFFECT_HOLDERS.register(name, () -> new PositiveOrNegativeEffectHolder(good));
    }
    public static RegistryObject<RegisteredPBEffect> registerBoxEffect(Class<? extends PBEffect> clazz, String name) {
        PBEffectRegistry.effectToStringID.put(clazz, name);
        return BOX_EFFECTS.register(name, () -> new RegisteredPBEffect(clazz));
    }

    public static void init(IEventBus bus) {
        FEATURES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        ENTITIES.register(bus);
        SERIALIZERS.register(bus);
        EFFECT_HOLDERS.register(bus);
        BOX_EFFECTS.register(bus);
    }
    public static final RegistryObject<DataSerializerEntry> PBEFFECTSERIALIZER = SERIALIZERS.register("box_effect", () -> new DataSerializerEntry(PandorasBoxEntity.PBEFFECT_SERIALIZER));
    private static final RegistryObject<PandoraLootModifier.Serializer> PANDORA = GLM.register("pandora", PandoraLootModifier.Serializer::new);
    public static final RegistryObject<PandorasBoxBlock> PB = BLOCKS.register("pandoras_box", PandorasBoxBlock::new);
    public static final RegistryObject<PandorasBoxItem> PBI = ITEMS.register("pandoras_box", () -> new PandorasBoxItem(PB.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<EntityType<PandorasBoxEntity>> Box = ENTITIES.register("pandoras_box", () -> EntityType.Builder.of(PandorasBoxEntity::new, EntityClassification.MISC).fireImmune().noSummon().sized(0.6f, 0.6f).build("pandoras_box"));
    public static final RegistryObject<TileEntityType<PandorasBoxBlockEntity>> TEPB = TILES.register("pandoras_box", () -> TileEntityType.Builder.of(PandorasBoxBlockEntity::new, PB.get()).build(null));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> LOLIPOP = FEATURES.register("lolipop", () -> new WorldGenLollipop(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> COLOURFUL_TREE = FEATURES.register("colourful_tree", () -> new WorldGenColorfulTree(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> RAINBOW = FEATURES.register("rainbow", () -> new WorldGenRainbow(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> MEGA_JUNGLE = FEATURES.register("mega_jungle", () -> new WorldGenMegaJungleCustom(BaseTreeFeatureConfig.CODEC, 20));
    static {
        makeFixedChance("matryoshka", 0.02);

        makePositiveOrNegative("mobs", false);
        makePositiveOrNegative("mob_towers", false);
        makePositiveOrNegative("megaton", false);
        makePositiveOrNegative("block_grave", false);
        makePositiveOrNegative("block_shower", false);
        makePositiveOrNegative("transform", false);
        makePositiveOrNegative("replace", false);
        makePositiveOrNegative("dryness", false);
        makePositiveOrNegative("tntsplosion", false);
        makePositiveOrNegative("dirty_trick", false);
        makePositiveOrNegative("water_pool", true);
        makePositiveOrNegative("lava_pool", false);
//        makePositiveOrNegative("gateway_to_hell_x", false);
//        makePositiveOrNegative("gateway_to_hell_z", false);
        makePositiveOrNegative("height_noise", false);
        makePositiveOrNegative("mad_geometry", false);
        makePositiveOrNegative("madder_geometry", false);
        makePositiveOrNegative("lava_cage", false);
        makePositiveOrNegative("water_cage", false);
        makePositiveOrNegative("classic", false);
        makePositiveOrNegative("lightning", false);
        makePositiveOrNegative("sand_for_dessert", false);
        makePositiveOrNegative("in_the_end", false);
        makePositiveOrNegative("hell_on_earth", false);
        makePositiveOrNegative("valley_of_souls", false);
        makePositiveOrNegative("deltas_of_destruction", false);
        makePositiveOrNegative("forest_of_calm", true);
        makePositiveOrNegative("forest_of_heat", false);
        makePositiveOrNegative("lifeless", false);
        makePositiveOrNegative("trapped_tribe", false);
        makePositiveOrNegative("buffed_down", false);
        makePositiveOrNegative("frozen_in_place", false);
        makePositiveOrNegative("flying_forest", false);
        makePositiveOrNegative("crush", false);
        makePositiveOrNegative("bomberman", false);
        makePositiveOrNegative("pitch_black", false);
        makePositiveOrNegative("void", false);
        makePositiveOrNegative("are_these_mine", false);
        makePositiveOrNegative("time_lord", false);
        makePositiveOrNegative("explocreatures", false);
        makePositiveOrNegative("explomobs", false);
        makePositiveOrNegative("aquarium", false);
        makePositiveOrNegative("aquaridoom", false);
        makePositiveOrNegative("world_snake", false);
        makePositiveOrNegative("double_snake", false);
        makePositiveOrNegative("tunnel_bore", false);
        makePositiveOrNegative("creeper_soul", false);
        makePositiveOrNegative("bombpack", false);
        makePositiveOrNegative("make_thin", false);
        makePositiveOrNegative("cover", false);
        makePositiveOrNegative("target", false);
        makePositiveOrNegative("armored_army", false);
        makePositiveOrNegative("army", false);
        makePositiveOrNegative("boss", false);
        makePositiveOrNegative("ice_age", false);
        makePositiveOrNegative("telerandom", false);
        makePositiveOrNegative("crazyport", false);
        makePositiveOrNegative("thing_go_boom", false);
        makePositiveOrNegativeFixedChance("apocalyptic_boom", 0.004, false);
        makePositiveOrNegative("danger_call", false);
        makePositiveOrNegative("animals", true);
        makePositiveOrNegative("animal_towers", true);
        makePositiveOrNegative("tamer", true);
        makePositiveOrNegative("items", true);
        makePositiveOrNegative("epic_armor", true);
        makePositiveOrNegative("epic_tool", true);
        makePositiveOrNegative("epic_thing", true);
        makePositiveOrNegative("enchanted_book", true);
        makePositiveOrNegative("resources", true);
        makePositiveOrNegative("equipment_set", true);
        makePositiveOrNegative("item_rain", true);
        makePositiveOrNegative("dead_creatures", true);
        makePositiveOrNegative("dead_mobs", true);
        makePositiveOrNegative("experience", true);
        makePositiveOrNegative("sudden_forest", true);
        makePositiveOrNegative("sudden_jungle", true);
        makePositiveOrNegative("odd_jungle", true);
        makePositiveOrNegative("buffed_up", true);
        makePositiveOrNegative("snow_age", true);
        makePositiveOrNegative("normal_land", true);
        makePositiveOrNegative("happy_fun_times", true);
        makePositiveOrNegative("shroomify", true);
        makePositiveOrNegative("rainbows", true);
        makePositiveOrNegative("all_rainbow", true);
        makePositiveOrNegative("farm", true);
        makePositiveOrNegative("cityscape", true);
        makePositiveOrNegative("heavenly", true);
        makePositiveOrNegative("halloween", true);
        makePositiveOrNegative("christmas", true);
        makePositiveOrNegative("new_year", true);
        makePositiveOrNegative("block_tower", true);
        makePositiveOrNegative("terrarium", true);
        makePositiveOrNegative("animal_farm", true);
        registerBoxEffect(PBEffectEntitiesBomberman.class, "entities_bomberman");
        registerBoxEffect(PBEffectEntitiesBuff.class, "entities_buff");
        registerBoxEffect(PBEffectEntitiesCrush.class, "entities_crush");
        registerBoxEffect(PBEffectEntitiesThrowItems.class, "entities_throw_items");
        registerBoxEffect(PBEffectGenConvertToDesert.class, "gen_convert_to_desert");
        registerBoxEffect(PBEffectGenConvertToEnd.class, "gen_convert_to_end");
        registerBoxEffect(PBEffectGenConvertToHalloween.class, "gen_convert_to_halloween");
        registerBoxEffect(PBEffectGenConvertToHFT.class, "gen_convert_to_hft");
        registerBoxEffect(PBEffectGenConvertToSnow.class, "gen_convert_to_snow");
        registerBoxEffect(PBEffectGenConvertToIce.class, "gen_convert_to_ice");
        registerBoxEffect(PBEffectGenConvertToMushroom.class, "gen_convert_to_mushroom");
        registerBoxEffect(PBEffectGenConvertToNether.class, "gen_convert_to_nether");
        registerBoxEffect(PBEffectGenConvertToOverworld.class, "gen_convert_to_overworld");
        registerBoxEffect(PBEffectGenConvertToChristmas.class, "gen_convert_to_christmas");
        registerBoxEffect(PBEffectGenConvertToFarm.class, "gen_convert_to_farm");
        registerBoxEffect(PBEffectGenConvertToHomo.class, "gen_convert_to_homo");
        registerBoxEffect(PBEffectGenConvertToLifeless.class, "gen_convert_to_lifeless");
        registerBoxEffect(PBEffectGenConvertToHeavenly.class, "gen_convert_to_heavenly");
        registerBoxEffect(PBEffectGenConvertToCity.class, "gen_convert_to_city");
        registerBoxEffect(PBEffectGenConvertToRainbowCloth.class, "gen_convert_to_rainbow_cloth");
        registerBoxEffect(PBEffectGenCreativeTowers.class, "gen_creative_towers");
        registerBoxEffect(PBEffectGenHeightNoise.class, "gen_height_noise");
        registerBoxEffect(PBEffectGenLavaCages.class, "gen_lava_cages");
        registerBoxEffect(PBEffectGenPool.class, "gen_pool");
        registerBoxEffect(PBEffectGenReplace.class, "gen_replace");
        registerBoxEffect(PBEffectGenRuinedPortal.class, "gen_ruined_portal");
        registerBoxEffect(PBEffectGenShapes.class, "gen_shapes");
        registerBoxEffect(PBEffectGenTransform.class, "gen_transform");
        registerBoxEffect(PBEffectGenTrees.class, "gen_trees");
        registerBoxEffect(PBEffectGenTreesOdd.class, "gen_trees_odd");
        registerBoxEffect(PBEffectMulti.class, "multi");
        registerBoxEffect(PBEffectSetTime.class, "set_time");
        registerBoxEffect(PBEffectSpawnBlocks.class, "spawn_blocks");
        registerBoxEffect(PBEffectSpawnEntityIDList.class, "spawn_entity_list");
        registerBoxEffect(PBEffectSpawnItemStacks.class, "spawn_item_stacks");
        registerBoxEffect(PBEffectRandomLightnings.class, "spawn_lightning");
        registerBoxEffect(PBEffectGenDome.class, "gen_dome");
        registerBoxEffect(PBEffectGenWorldSnake.class, "gen_world_snake");
        registerBoxEffect(PBEffectSetWeather.class, "set_weather");
        registerBoxEffect(PBEffectRandomExplosions.class, "random_explosions");
        registerBoxEffect(PBEffectEntitiesBombpack.class, "entities_bomb_pack");
        registerBoxEffect(PBEffectGenCover.class, "gen_cover");
        registerBoxEffect(PBEffectGenTargets.class, "gen_targets");
        registerBoxEffect(PBEffectEntitiesCreateVoid.class, "entities_gen_void");
        registerBoxEffect(PBEffectEntitiesTeleport.class, "entities_teleport");
        registerBoxEffect(PBEffectDuplicateBox.class, "duplicate_box");
        registerBoxEffect(PBEffectExplode.class, "explode");
    }
}
