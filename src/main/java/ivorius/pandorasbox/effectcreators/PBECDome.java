/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenDome;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECDome(IValue time, DValue range, List<Either<WeightedBlock, WeightedTag<Block>>> domeBlocks, Optional<Block> fillBlock) implements PBEffectCreator {
    public static final MapCodec<PBECDome> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECDome::time),
                            DValue.CODEC.fieldOf("range").forGetter(PBECDome::range),
                            WeightedBlock.CODEC.fieldOf("floor_blocks").forGetter(PBECDome::domeBlocks),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fill_block").forGetter(PBECDome::fillBlock))
                    .apply(instance, PBECDome::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = this.time.getValue(random);

        Block domeBlock = PandorasBoxHelper.getRandomBlock(random, PandorasBoxHelper.assembleBlocks(domeBlocks));

        return new PBEffectGenDome(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), domeBlock, fillBlock.orElse(null));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
