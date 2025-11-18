package ivorius.pandorasbox.effects.generate.entity_spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface EntitySpawner {
    Codec<EntitySpawner> CODEC = Init.ENTITY_SPAWNER_TYPE_REGISTRY.get().getCodec()
            .dispatch(EntitySpawner::codec, MapCodec::codec);
    void spawnEntities(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, int unifiedSeed);
    @NotNull MapCodec<? extends EntitySpawner> codec();
}
