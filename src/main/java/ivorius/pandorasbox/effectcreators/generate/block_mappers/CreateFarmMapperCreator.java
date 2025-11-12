package ivorius.pandorasbox.effectcreators.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.CreateFarmMapper;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record CreateFarmMapperCreator(DValue cropChance) implements BlockMapperCreator {
    public static final MapCodec<CreateFarmMapperCreator> CODEC = DValue.CODEC.fieldOf("crop_chance").xmap(CreateFarmMapperCreator::new, CreateFarmMapperCreator::cropChance).fieldOf("crop_chance");

    @Override
    public BlockMapper constructBlockMapper(Level world, double x, double y, double z, RandomSource random) {
        return new CreateFarmMapper(this.cropChance.getValue(random));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapperCreator> codec() {
        return CODEC;
    }
}
