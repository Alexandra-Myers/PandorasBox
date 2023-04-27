package ivorius.pandorasbox.worldgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillageStructure;

public class SurfaceStructure extends VillageStructure {
    public SurfaceStructure(Codec<VillageConfig> p_i232001_1_) {
        super(p_i232001_1_);
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }
}
