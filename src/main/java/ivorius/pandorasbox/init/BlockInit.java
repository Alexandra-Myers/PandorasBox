package ivorius.pandorasbox.init;

import ivorius.pandorasbox.block.PandorasBoxBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class BlockInit {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<PandorasBoxBlock> PB = register("pandoras_box", PandorasBoxBlock::new);
    public static final RegistryObject<InfestedBlock> INFESTED_END_STONE = register("infested_end_stone", () -> new InfestedBlock(Blocks.END_STONE, BlockBehaviour.Properties.copy(Blocks.END_STONE)));
    public static final RegistryObject<Block> END_STONE_SLAB = register("end_stone_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.END_STONE)));
    public static final RegistryObject<Block> END_STONE_STAIRS = register("end_stone_stairs", () -> new StairBlock(Blocks.END_STONE::defaultBlockState, BlockBehaviour.Properties.copy(Blocks.END_STONE)));
    public static final RegistryObject<Block> END_STONE_WALL = register("end_stone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.END_STONE).forceSolidOn()));
    public static final RegistryObject<Block> CHISELED_END_STONE_BRICKS = register("chiseled_end_stone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)));
    public static final RegistryObject<InfestedBlock> INFESTED_CHISELED_END_STONE_BRICKS = register("infested_chiseled_end_stone_bricks", () -> new InfestedBlock(CHISELED_END_STONE_BRICKS.get(), BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)));
    public static final RegistryObject<InfestedBlock> INFESTED_END_STONE_BRICKS = register("infested_end_stone_bricks", () -> new InfestedBlock(Blocks.END_STONE_BRICKS, BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)));
    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
    public static void registerBlocks(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
