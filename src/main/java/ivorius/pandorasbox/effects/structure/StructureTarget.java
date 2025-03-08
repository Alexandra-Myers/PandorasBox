package ivorius.pandorasbox.effects.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.Vec3i;

public class StructureTarget extends Structure {
    public static final Codec<StructureTarget> CODEC = produceCodec(TargetConfiguration.CODEC, StructureTarget::new);
    public StructureTarget() {
    }

    public StructureTarget(float structureStart, float structureLength, Vec3i pos, int unifiedSeed, StructureConfiguration configuration) {
        super(structureStart, structureLength, pos, unifiedSeed, configuration);
    }

    public Integer[] getColors() {
        return !(configuration instanceof TargetConfiguration targetConfiguration) ? new Integer[0] : targetConfiguration.colors();
    }
}