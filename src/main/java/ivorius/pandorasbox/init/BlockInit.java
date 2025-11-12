package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class BlockInit {
    public static final PandorasBoxBlock PB = register("pandoras_box", PandorasBoxBlock::new);
    private static <T extends Block> T register(String name, Function<ResourceKey<Block>, T> block) {
        ResourceKey<Block> blockResourceKey = ResourceKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name));
        return Registry.register(BuiltInRegistries.BLOCK, blockResourceKey, block.apply(blockResourceKey));
    }
    public static void registerBlocks() {

    }
}
