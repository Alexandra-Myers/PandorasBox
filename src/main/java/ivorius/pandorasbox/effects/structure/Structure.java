package ivorius.pandorasbox.effects.structure;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.NotNull;

public class Structure {
    public static final Codec<Structure> CODEC = produceCodec(Empty.CODEC, Structure::new);
    public float structureStart;
    public float structureLength;

    public Vec3i pos;

    public int unifiedSeed;
    public StructureConfiguration configuration;

    public static <T extends Structure, A extends StructureConfiguration> Codec<T> produceCodec(Codec<A> codec, Function5<Float, Float, Vec3i, Integer, StructureConfiguration, T> constructor) {
        return RecordCodecBuilder.create(instance ->
                instance.group(Codec.FLOAT.fieldOf("structure_start").forGetter(Structure::getStructureStart),
                                Codec.FLOAT.fieldOf("structure_length").forGetter(Structure::getStructureLength),
                                Vec3i.CODEC.fieldOf("pos").forGetter(Structure::getPos),
                                Codec.INT.fieldOf("unified_seed").forGetter(Structure::getUnifiedSeed),
                                codec.fieldOf("config").forGetter(Structure::<A>getConfiguration))
                        .apply(instance, constructor::apply));
    }

    public Structure() {

    }

    public Structure(float structureStart, float structureLength, Vec3i pos, int unifiedSeed, StructureConfiguration configuration) {
        this.structureStart = structureStart;
        this.structureLength = structureLength;
        this.pos = pos;
        this.unifiedSeed = unifiedSeed;
        this.configuration = configuration;
    }

    public float getStructureStart() {
        return structureStart;
    }

    public float getStructureLength() {
        return structureLength;
    }

    public Vec3i getPos() {
        return pos;
    }

    public int getUnifiedSeed() {
        return unifiedSeed;
    }

    @SuppressWarnings("unchecked")
    public <A extends StructureConfiguration> A getConfiguration() {
        return (A) configuration;
    }

    public interface StructureConfiguration {
        @NotNull Codec<? extends StructureConfiguration> codec();
    }

    public record Empty() implements StructureConfiguration {
        public static final Codec<Empty> CODEC = Codec.unit(Empty::new);

        @Override
        public @NotNull Codec<? extends StructureConfiguration> codec() {
            return CODEC;
        }
    }
}