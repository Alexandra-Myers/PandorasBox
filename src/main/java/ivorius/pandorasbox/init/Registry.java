package ivorius.pandorasbox.init;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlock;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.items.PandorasBoxItem;
import ivorius.pandorasbox.utils.PBEffectArgument;
import ivorius.pandorasbox.worldgen.*;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class Registry {

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, MOD_ID);
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, MOD_ID);
    private static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, MOD_ID);
//    private static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_FEATURES, MOD_ID);

    public static void init(IEventBus bus) {
        FEATURES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        ENTITIES.register(bus);
        SERIALIZERS.register(bus);
        ARGUMENTS.register(bus);
//        STRUCTURES.register(bus);
    }
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<PBEffect>> PBEFFECTSERIALIZER = SERIALIZERS.register("box_effect", () -> PandorasBoxEntity.PBEFFECT_SERIALIZER);
//    public static final DeferredHolder<Structure<VillageConfig>> SHRINE_STRUCTURE = STRUCTURES.register("shrine", () -> new SurfaceStructure(VillageConfig.CODEC));
//    public static final JigsawPattern START = JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("pandorasbox:shrine/start_pool"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("pandorasbox:shrine", ProcessorLists.EMPTY), 1), Pair.of(JigsawPiece.single("pandorasbox:shrine_small", ProcessorLists.EMPTY), 100)), JigsawPattern.PlacementBehaviour.RIGID));

    private static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<PandoraLootModifier>> PANDORA = GLM.register("pandora", PandoraLootModifier.CODEC);
    public static final DeferredHolder<Block, PandorasBoxBlock> PB = BLOCKS.register("pandoras_box", PandorasBoxBlock::new);
    public static final DeferredHolder<Item, PandorasBoxItem> PBI = ITEMS.register("pandoras_box", () -> new PandorasBoxItem(PB.get(), new Item.Properties()));
    public static final DeferredHolder<EntityType<?>, EntityType<PandorasBoxEntity>> Box = ENTITIES.register("pandoras_box", () -> EntityType.Builder.of(PandorasBoxEntity::new, MobCategory.MISC).fireImmune().noSummon().sized(0.6f, 0.6f).build("pandoras_box"));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PandorasBoxBlockEntity>> TEPB = TILES.register("pandoras_box", () -> BlockEntityType.Builder.of(PandorasBoxBlockEntity::new, PB.get()).build(null));
    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> LOLIPOP = FEATURES.register("lolipop", () -> new WorldGenLollipop(TreeConfiguration.CODEC, 20));
    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> COLOURFUL_TREE = FEATURES.register("colourful_tree", () -> new WorldGenColorfulTree(TreeConfiguration.CODEC, 20));
    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> RAINBOW = FEATURES.register("rainbow", () -> new WorldGenRainbow(TreeConfiguration.CODEC, 20));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<PBEffectArgument>> PBEFFECTARGUMENT = ARGUMENTS.register("pbeffect", () -> ArgumentTypeInfos.registerByClass(PBEffectArgument.class, SingletonArgumentInfo.contextFree(PBEffectArgument::effect)));
    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> MEGA_JUNGLE = FEATURES.register("mega_jungle", () -> new WorldGenMegaJungleCustom(TreeConfiguration.CODEC, 20));
}
