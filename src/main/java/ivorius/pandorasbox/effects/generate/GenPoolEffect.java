package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.BlockPositions;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static ivorius.pandorasbox.effects.PBEffect.setBlockVarying;

public record GenPoolEffect(Block fillBlock, Block platformBlock) implements GenerateEffect {
    public static final MapCodec<GenPoolEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("fill_block").forGetter(GenPoolEffect::fillBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("platform_block").forGetter(GenPoolEffect::platformBlock))
                    .apply(instance, GenPoolEffect::new));
    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (!level.isClientSide && !level.getBlockState(pos).isAir()) {
            boolean setPlatform = false;
            if (platformBlock != null && !platformBlock.defaultBlockState().isAir()) {
                List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, BlockPositions.expandToAABB(pos, 2.5, 2.5, 2.5));

                if (!livingEntities.isEmpty())
                    setPlatform = true;
            }

            if (setPlatform)
                setBlockVarying(level, pos, platformBlock, unifiedSeed);
            else
                setBlockVarying(level, pos, fillBlock, unifiedSeed);
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
