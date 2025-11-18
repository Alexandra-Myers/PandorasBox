package ivorius.pandorasbox.init;

import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;
import static ivorius.pandorasbox.init.BlockInit.PB;

public class BlockEntityInit {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    public static final RegistryObject<BlockEntityType<PandorasBoxBlockEntity>> BEPB = register("pandoras_box", () -> BlockEntityType.Builder.of(PandorasBoxBlockEntity::new, PB.get()).build(null));
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> blockEntityType) {
        return BLOCK_ENTITIES.register(name, blockEntityType);
    }
    public static void registerBlockEntities(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
