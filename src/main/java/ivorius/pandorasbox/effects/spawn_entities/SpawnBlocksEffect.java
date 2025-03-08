package ivorius.pandorasbox.effects.spawn_entities;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record SpawnBlocksEffect(Block[] blocks, EntitySpawnConfiguration entitySpawnConfiguration) implements SpawnEntitiesEffect {
    public static final MapCodec<SpawnBlocksEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).fieldOf("blocks").forGetter(SpawnBlocksEffect::blocks),
                            EntitySpawnConfiguration.MAP_CODEC.forGetter(SpawnBlocksEffect::entitySpawnConfiguration))
                    .apply(instance, SpawnBlocksEffect::new));

    @Override
    public Entity spawnEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, int number, double x, double y, double z) {
        if (world.isClientSide()) return null;
        Block block = blocks[number];

        return FallingBlockEntity.fall(world, BlockPos.containing(x, y, z), block.defaultBlockState());
    }

    @Override
    public @NotNull MapCodec<? extends SpawnEntitiesEffect> codec() {
        return CODEC;
    }
}
