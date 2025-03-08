package ivorius.pandorasbox.effects.structure;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record CreativeTowerConfiguration(Block[] blocks) implements Structure.StructureConfiguration {
    public static final Codec<CreativeTowerConfiguration> CODEC = PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).xmap(CreativeTowerConfiguration::new, CreativeTowerConfiguration::blocks);

    @Override
    public @NotNull Codec<? extends Structure.StructureConfiguration> codec() {
        return CODEC;
    }
}