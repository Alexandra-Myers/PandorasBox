package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockInit {
    public static final PandorasBoxBlock PB = register("pandoras_box", new PandorasBoxBlock());
    private static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, ResourceKey.create(BuiltInRegistries.BLOCK.key(), new ResourceLocation(PandorasBox.MOD_ID, name)), block);
    }
    public static void registerBlocks() {

    }
}
