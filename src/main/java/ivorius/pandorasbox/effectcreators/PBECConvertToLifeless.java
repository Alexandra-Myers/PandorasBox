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
import ivorius.pandorasbox.effects.generate.block_mappers.DryMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.SimpleConvertMapper;
import ivorius.pandorasbox.random.DValue;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToLifeless(DValue range) implements PBEffectCreator {
    public static final List<BlockMapper> LIFELESS_MAPPERS;
    static {
        LIFELESS_MAPPERS = new ArrayList<>();
        LIFELESS_MAPPERS.add(new DryMapper());
        LIFELESS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.LAVA), Either.right(BlockTags.SNOW), Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, Blocks.AIR));
        LIFELESS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.FLOWERS), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS)}, Blocks.DEAD_BUSH));
        LIFELESS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.WOOL), Either.left(Blocks.CAKE)}, Blocks.DIRT));
        LIFELESS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}, Blocks.STONE));
        LIFELESS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.SAND)}, Blocks.SAND));
    }
    public static final MapCodec<PBECConvertToLifeless> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToLifeless::new, PBECConvertToLifeless::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 1, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.BADLANDS), LIFELESS_MAPPERS, Collections.emptyList(), Collections.emptyList()));
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
