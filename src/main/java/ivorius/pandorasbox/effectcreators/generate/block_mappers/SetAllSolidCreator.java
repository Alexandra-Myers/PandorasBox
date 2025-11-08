package ivorius.pandorasbox.effectcreators.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.SetAllSolid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SetAllSolidCreator(Block toReplace, Optional<BlockMapperCreator> otherwise) implements BlockMapperCreator {
    public static final MapCodec<SetAllSolidCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_replace").forGetter(SetAllSolidCreator::toReplace),
                            BlockMapperCreator.CODEC.optionalFieldOf("otherwise").forGetter(SetAllSolidCreator::otherwise))
                    .apply(instance, SetAllSolidCreator::new));

    @Override
    public BlockMapper constructBlockMapper(Level world, double x, double y, double z, RandomSource random) {
        return new SetAllSolid(toReplace, otherwise.map(blockMapperCreator -> blockMapperCreator.constructBlockMapper(world, x, y, z, random)));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapperCreator> codec() {
        return CODEC;
    }
}
