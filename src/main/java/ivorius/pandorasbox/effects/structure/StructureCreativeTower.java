package ivorius.pandorasbox.effects.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;

public class StructureCreativeTower extends Structure {
    public static final Codec<StructureCreativeTower> CODEC = produceCodec(CreativeTowerConfiguration.CODEC, StructureCreativeTower::new);
    public StructureCreativeTower() {
    }

    public StructureCreativeTower(float structureStart, float structureLength, Vec3i pos, int unifiedSeed, StructureConfiguration configuration) {
        super(structureStart, structureLength, pos, unifiedSeed, configuration);
    }

    public Block[] getBlocks() {
        return !(configuration instanceof CreativeTowerConfiguration creativeTowerConfiguration) ? new Block[0] : creativeTowerConfiguration.blocks();
    }
}