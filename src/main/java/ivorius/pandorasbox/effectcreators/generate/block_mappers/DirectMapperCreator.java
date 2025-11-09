package ivorius.pandorasbox.effectcreators.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record DirectMapperCreator(BlockMapper mapper) implements BlockMapperCreator {
    public static final MapCodec<DirectMapperCreator> CODEC = BlockMapper.MAP_CODEC.xmap(DirectMapperCreator::new, DirectMapperCreator::mapper);
    @Override
    public BlockMapper constructBlockMapper(Level world, double x, double y, double z, RandomSource random) {
        return mapper;
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapperCreator> codec() {
        return CODEC;
    }
}
