/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.RandomTaggedMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.SetAllSolid;
import ivorius.pandorasbox.effects.generate.block_mappers.SimpleConvertMapper;
import ivorius.pandorasbox.effects.generate.feature_generators.GenerateHFT;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToHFT(DValue range) implements PBEffectCreator {
    public static final Either<Block, TagKey<Block>>[] EXCLUDED_TARGETS = new Either[]{Either.right(BlockTags.WOOL), Either.right(PandorasBox.ALL_TERRACOTTA)};
    public static final List<BlockMapper> HFT_MAPPERS;
    static {
        HFT_MAPPERS = new ArrayList<>();
        HFT_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.LAVA), Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES)}, Blocks.AIR));
    }
    public static final MapCodec<PBECConvertToHFT> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToHFT::new, PBECConvertToHFT::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Integer[] metaTypes = new Integer[random.nextInt(3) + 2];
        for (int i = 0; i < metaTypes.length; i++) {
            metaTypes[i] = random.nextInt(32);
        }
        List<BlockMapper> adjMappers = new ArrayList<>(HFT_MAPPERS);
        adjMappers.add(new SetAllSolid(Blocks.AIR, Optional.of(new RandomTaggedMapper(Optional.empty(), PandorasBox.ALL_TERRACOTTA, metaTypes))));

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.CHERRY_GROVE), EXCLUDED_TARGETS, adjMappers, Collections.singletonList(new GenerateHFT(metaTypes)), Collections.emptyList()));
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
