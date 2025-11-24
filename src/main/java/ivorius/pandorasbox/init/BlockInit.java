package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class BlockInit {
    public static final PandorasBoxBlock PB = register("pandoras_box", resourceKey -> new PandorasBoxBlock());
    public static final InfestedBlock INFESTED_END_STONE = register("infested_end_stone", resourceKey -> new InfestedBlock(Blocks.END_STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE)));
    public static final Block END_STONE_SLAB = register("end_stone_slab", resourceKey -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE)));
    public static final Block END_STONE_STAIRS = register("end_stone_stairs", resourceKey -> new StairBlock(Blocks.END_STONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE)));
    public static final Block END_STONE_WALL = register("end_stone_wall", resourceKey -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE).forceSolidOn()));
    public static final Block CHISELED_END_STONE_BRICKS = register("chiseled_end_stone_bricks", resourceKey -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));
    public static final InfestedBlock INFESTED_CHISELED_END_STONE_BRICKS = register("infested_chiseled_end_stone_bricks", resourceKey -> new InfestedBlock(CHISELED_END_STONE_BRICKS, BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));
    public static final InfestedBlock INFESTED_END_STONE_BRICKS = register("infested_end_stone_bricks", resourceKey -> new InfestedBlock(Blocks.END_STONE_BRICKS, BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));
    private static <T extends Block> T register(String name, Function<ResourceKey<Block>, T> block) {
        ResourceKey<Block> blockResourceKey = ResourceKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name));
        return Registry.register(BuiltInRegistries.BLOCK, blockResourceKey, block.apply(blockResourceKey));
    }
    public static void registerBlocks() {

    }
}
