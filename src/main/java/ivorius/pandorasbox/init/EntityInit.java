package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityInit {
    public static final EntityType<PandorasBoxEntity> BOX = register("pandoras_box", EntityType.Builder.<PandorasBoxEntity>of(PandorasBoxEntity::new, MobCategory.MISC).fireImmune().noSummon().sized(0.6f, 0.6f).build("pandoras_box"));
    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> entityType) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), entityType);
    }
    public static void registerEntities() {

    }
}
