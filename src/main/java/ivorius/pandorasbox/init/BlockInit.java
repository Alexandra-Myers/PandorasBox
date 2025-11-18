package ivorius.pandorasbox.init;

import ivorius.pandorasbox.block.PandorasBoxBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class BlockInit {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<PandorasBoxBlock> PB = register("pandoras_box", PandorasBoxBlock::new);
    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
    public static void registerBlocks(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
