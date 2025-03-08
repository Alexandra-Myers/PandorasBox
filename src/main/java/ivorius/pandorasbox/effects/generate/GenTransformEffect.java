package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ivorius.pandorasbox.effects.PBEffect.setBlockVarying;

public record GenTransformEffect(Block[] blocks) implements GenerateEffect {
    public static final MapCodec<GenTransformEffect> CODEC = PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).fieldOf("blocks").xmap(GenTransformEffect::new, GenTransformEffect::blocks);
    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (!level.isClientSide) {
            Block block = blocks[random.nextInt(blocks.length)];

            if (level.loadedAndEntityCanStandOn(pos, entity)) {
                setBlockVarying(level, pos, block, unifiedSeed);
            }
        }
    }

    @Override
    public @Nullable ResourceKey<Biome> biome() {
        return null;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffect> codec() {
        return CODEC;
    }
}
