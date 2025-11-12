package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.FunctionalGiant;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityInit {
    public static final EntityType<FunctionalGiant> GIANT = register("giant", EntityType.Builder.of(FunctionalGiant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).eyeHeight(10.44F).ridingOffset(-3.75F).clientTrackingRange(10));
    public static final EntityType<PandorasBoxEntity> BOX = register("pandoras_box", EntityType.Builder.<PandorasBoxEntity>of(PandorasBoxEntity::new, MobCategory.MISC).fireImmune().noSummon().sized(0.6f, 0.6f));
    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> entityType) {
        ResourceKey<EntityType<?>> resourceKey = ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceKey, entityType.build(name));
    }
    public static void registerEntities() {
        FabricDefaultAttributeRegistry.register(GIANT, FunctionalGiant.createGiantAttributes());
    }
}
