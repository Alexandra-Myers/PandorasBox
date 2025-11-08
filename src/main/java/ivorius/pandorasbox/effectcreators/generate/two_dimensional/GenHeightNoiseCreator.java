package ivorius.pandorasbox.effectcreators.generate.two_dimensional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.two_dimensional.GenHeightNoise;
import ivorius.pandorasbox.effects.generate.two_dimensional.Generate2D;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record GenHeightNoiseCreator(IValue shift, IValue towerSize, IValue blockSize) implements Generate2DCreator {
    public static final MapCodec<GenHeightNoiseCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("shift").forGetter(GenHeightNoiseCreator::shift),
                            IValue.CODEC.fieldOf("tower_size").forGetter(GenHeightNoiseCreator::towerSize),
                            IValue.CODEC.fieldOf("block_size").forGetter(GenHeightNoiseCreator::blockSize))
                    .apply(instance, GenHeightNoiseCreator::new));

    @Override
    public Generate2D constructGenerate2D(Level world, double x, double y, double z, RandomSource random) {
        int blockSize = this.blockSize.getValue(random);

        int[] shift = ValueHelper.getValueRange(this.shift, random);
        int[] towerSize = ValueHelper.getValueRange(this.towerSize, random);
        return new GenHeightNoise(shift[0], shift[1], towerSize[0], towerSize[1], blockSize);
    }

    @Override
    public @NotNull MapCodec<? extends Generate2DCreator> codec() {
        return CODEC;
    }
}
