package ivorius.pandorasbox.init;

import ivorius.pandorasbox.entitites.FunctionalGiant;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class EntityInit {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public static final RegistryObject<EntityType<FunctionalGiant>> GIANT = register("giant", name -> {
        EntityType<FunctionalGiant> builtType = EntityType.Builder.of(FunctionalGiant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).clientTrackingRange(10).build(name);
        FabricDefaultAttributeRegistry.register(builtType, FunctionalGiant.createGiantAttributes());
        return builtType;
    });
    public static final RegistryObject<EntityType<PandorasBoxEntity>> BOX = register("pandoras_box", name -> EntityType.Builder.<PandorasBoxEntity>of(PandorasBoxEntity::new, MobCategory.MISC).fireImmune().noSummon().sized(0.6f, 0.6f).build(name));
    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Function<String, EntityType<T>> entityType) {
        return ENTITIES.register(name, () -> entityType.apply(name));
    }
    public static void registerEntities(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
