package ivorius.pandorasbox.effects.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record ShapeConfiguration(Block[] blocks, int shapeType, double size) implements Structure.StructureConfiguration {
    public static final Codec<ShapeConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).fieldOf("blocks").forGetter(ShapeConfiguration::blocks),
                            Codec.INT.fieldOf("shape_type").forGetter(ShapeConfiguration::shapeType),
                            Codec.DOUBLE.fieldOf("size").forGetter(ShapeConfiguration::size))
                    .apply(instance, ShapeConfiguration::new));
    @Override
    public @NotNull Codec<? extends Structure.StructureConfiguration> codec() {
        return CODEC;
    }
}
