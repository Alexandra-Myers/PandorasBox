package ivorius.pandorasbox.effects.spawn_entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface SpawnEntitiesEffect {
    Codec<SpawnEntitiesEffect> CODEC = Init.SPAWN_ENTITIES_EFFECT_TYPE_REGISTRY.get().getCodec()
            .dispatch(SpawnEntitiesEffect::codec, MapCodec::codec);
    Entity spawnEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, int number, double x, double y, double z);
    EntitySpawnConfiguration entitySpawnConfiguration();
    default boolean spawnsFromBox() {
        return entitySpawnConfiguration().spawnsFromBox();
    }
    default boolean spawnDirect() {
        return entitySpawnConfiguration().spawnDirect();
    }
    default double range() {
        return entitySpawnConfiguration().range();
    }
    default double shiftY() {
        return entitySpawnConfiguration().shiftY();
    }
    default double throwStrengthSideMin() {
        return entitySpawnConfiguration().throwStrengthSideMin();
    }
    default double throwStrengthSideMax() {
        return entitySpawnConfiguration().throwStrengthSideMax();
    }
    default double throwStrengthYMin() {
        return entitySpawnConfiguration().throwStrengthYMin();
    }
    default double throwStrengthYMax() {
        return entitySpawnConfiguration().throwStrengthYMax();
    }
    @NotNull MapCodec<? extends SpawnEntitiesEffect> codec();
}
