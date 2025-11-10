package ivorius.pandorasbox.effectcreators.generate.block_mappers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.init.Init;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BlockMapperCreator {
    Codec<BlockMapperCreator> CODEC = Codec.withAlternative(Init.BLOCK_MAPPER_CREATOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(BlockMapperCreator::codec, Function.identity()), BlockMapper.CODEC.xmap(DirectMapperCreator::new, DirectMapperCreator::mapper));
    BlockMapper constructBlockMapper(Level world, double x, double y, double z, RandomSource random);
    @NotNull MapCodec<? extends BlockMapperCreator> codec();
}
