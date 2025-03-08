package ivorius.pandorasbox.effects.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;

public class StructureShape extends Structure {
    public static final Codec<StructureShape> CODEC = produceCodec(ShapeConfiguration.CODEC, StructureShape::new);
    public StructureShape() {
    }

    public StructureShape(float structureStart, float structureLength, Vec3i pos, int unifiedSeed, StructureConfiguration configuration) {
        super(structureStart, structureLength, pos, unifiedSeed, configuration);
    }

    public Block[] getBlocks() {
        return !(configuration instanceof ShapeConfiguration shapeConfiguration) ? new Block[0] : shapeConfiguration.blocks();
    }

    public int getShapeType() {
        return !(configuration instanceof ShapeConfiguration shapeConfiguration) ? 0 : shapeConfiguration.shapeType();
    }

    public double getSize() {
        return !(configuration instanceof ShapeConfiguration shapeConfiguration) ? 0 : shapeConfiguration.size();
    }
}