package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record RangeTaggedMapper(TagKey<Block> tagKey, Integer[] tagMetas, double ringSize) implements BlockMapper {
    public static final MapCodec<RangeTaggedMapper> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(RangeTaggedMapper::tagKey),
                            PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("metas").forGetter(RangeTaggedMapper::tagMetas),
                            Codec.DOUBLE.fieldOf("ring_size").forGetter(RangeTaggedMapper::ringSize))
                    .apply(instance, RangeTaggedMapper::new));
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return serverLevel.loadedAndEntityCanStandOn(blockPos, entity) && serverLevel.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        double dist = Mth.sqrt((float) effectCenter.distanceToSqr(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)));
        HolderSet.Named<Block> tag = BuiltInRegistries.BLOCK.getOrCreateTag(tagKey);
        setBlockSafe(serverLevel, blockPos, tag.get(tagMetas[Mth.floor(dist / ringSize) % tagMetas.length]).value().defaultBlockState());
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
