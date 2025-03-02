/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Created by lukas on 31.03.14.
 */
public record WeightedBlock(double weight, Holder<Block> block) implements WeightedSelector.Item {
    public static final Codec<WeightedBlock> BLOCK_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedBlock::weight),
                        BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(WeightedBlock::block))
                    .apply(instance, WeightedBlock::new));
    public static final Codec<List<Either<WeightedBlock, WeightedTag<Block>>>> CODEC = Codec.either(BLOCK_CODEC, WeightedTag.codec(Registries.BLOCK)).listOf();
    public WeightedBlock(double weight, Block block) {
        this(weight, BuiltInRegistries.BLOCK.wrapAsHolder(block));
    }
}
