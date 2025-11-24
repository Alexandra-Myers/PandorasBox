package ivorius.pandorasbox.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import static ivorius.pandorasbox.init.PandoraItemTags.register;

public class PandoraStructureTags {
    public static final TagKey<Structure> BLOCKS_NEARBY_TREES = register(Registries.STRUCTURE, "blocks_nearby_trees");
}