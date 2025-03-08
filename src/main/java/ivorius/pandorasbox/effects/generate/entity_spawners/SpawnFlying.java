package ivorius.pandorasbox.effects.generate.entity_spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.*;

public record SpawnFlying(String entityID, float chance) implements EntitySpawner {
    public static final MapCodec<SpawnFlying> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(WeightedEntity.ID_CODEC.fieldOf("entity_id").forGetter(SpawnFlying::entityID),
                            Codec.FLOAT.fieldOf("chance").forGetter(SpawnFlying::chance))
                    .apply(instance, SpawnFlying::new));
    @Override
    public void spawnEntities(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, int unifiedSeed) {
        if (canSpawnFlyingEntity(level, level.getBlockState(pos), pos)) {
            lazilySpawnFlyingEntity(level, entity, random, entityID, chance, pos);
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntitySpawner> codec() {
        return CODEC;
    }
}
