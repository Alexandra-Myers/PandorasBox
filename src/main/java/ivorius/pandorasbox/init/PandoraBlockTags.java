package ivorius.pandorasbox.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class PandoraBlockTags {
    public static final TagKey<Block> OBSIDIANS = PandoraItemTags.registerC(Registries.BLOCK, "obsidians");
    public static final TagKey<Block> NORMAL_OBSIDIANS = PandoraItemTags.registerC(Registries.BLOCK, "obsidians/normal");
    public static final TagKey<Block> CRYING_OBSIDIANS = PandoraItemTags.registerC(Registries.BLOCK, "obsidians/crying");
    public static final TagKey<Block> ALL_TERRACOTTA = PandoraItemTags.register(Registries.BLOCK, "all_terracotta");
}
