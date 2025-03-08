package ivorius.pandorasbox.effects.generate.entity_spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.canSpawnEntity;
import static ivorius.pandorasbox.effects.PBEffect.lazilySpawnEntity;

public record SpawnRandom(String entityID, float chance) implements EntitySpawner {
    public static final MapCodec<SpawnRandom> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(WeightedEntity.ID_CODEC.fieldOf("entity_id").forGetter(SpawnRandom::entityID),
                            Codec.FLOAT.fieldOf("chance").forGetter(SpawnRandom::chance))
                    .apply(instance, SpawnRandom::new));
    @Override
    public void spawnEntities(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, int unifiedSeed) {
        Entity generatedEntity = lazilySpawnEntity(level, entity, random, entityID, chance, pos);
        canSpawnEntity(level, level.getBlockState(pos), pos, generatedEntity);
    }

    @Override
    public @NotNull MapCodec<? extends EntitySpawner> codec() {
        return CODEC;
    }
}
