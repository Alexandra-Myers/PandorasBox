package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static ivorius.pandorasbox.init.BlockInit.PB;

public class BlockEntityInit {
    public static final BlockEntityType<PandorasBoxBlockEntity> BEPB = register("pandoras_box", BlockEntityType.Builder.of(PandorasBoxBlockEntity::new, PB).build(null));
    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> blockEntityType) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), blockEntityType);
    }
    public static void registerBlockEntities() {

    }
}
