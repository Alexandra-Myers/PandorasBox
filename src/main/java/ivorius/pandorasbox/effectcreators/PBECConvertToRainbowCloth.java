/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.RangeTaggedMapper;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToRainbowCloth(DValue range, IValue rainbowComplexity, DValue ringSize, TagKey<Block> tag) implements PBEffectCreator {
    public static final MapCodec<PBECConvertToRainbowCloth> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECConvertToRainbowCloth::range),
                            IValue.CODEC.fieldOf("rainbow_complexity").forGetter(PBECConvertToRainbowCloth::rainbowComplexity),
                            DValue.CODEC.fieldOf("ring_size").forGetter(PBECConvertToRainbowCloth::ringSize),
                            TagKey.codec(Registries.BLOCK).optionalFieldOf("tag", BlockTags.WOOL).forGetter(PBECConvertToRainbowCloth::tag))
                    .apply(instance, PBECConvertToRainbowCloth::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        int rainbowComplexity = this.rainbowComplexity.getValue(random);
        double ringSize = this.ringSize.getValue(random);

        Integer[] colors = new Integer[rainbowComplexity];
        for (int i = 0; i < colors.length; i++)
            colors[i] = random.nextInt(BuiltInRegistries.BLOCK.get(tag).map(HolderSet.ListBacked::size).orElse(16));

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.empty(), Collections.singletonList(new RangeTaggedMapper(tag, colors, ringSize)), Collections.emptyList(), Collections.emptyList(), null));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
