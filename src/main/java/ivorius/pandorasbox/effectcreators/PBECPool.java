/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenPool;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECPool(DValue range, Block block, EitherArrayList<WeightedBlock, WeightedTag<Block>> platformBlocks) implements PBEffectCreator {
    public static final MapCodec<PBECPool> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECPool::range),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(PBECPool::block),
                        WeightedBlock.CODEC.fieldOf("platform_blocks").forGetter(PBECPool::platformBlocks))
                .apply(instance, PBECPool::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Block platformBlock = platformBlocks.isEmpty() ? Blocks.AIR : PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(platformBlocks));

        return new PBEffectGenPool(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), block, platformBlock);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
