/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenRuinedPortal;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexandra on 27.04.23.
 */
public record PBECRuinedPortal(IValue rangeH, IValue rangeY, IValue rangeStartY, WeightedBlock[][] brickSet, List<RandomizedItemStack> loot) implements PBEffectCreator {
    public static final MapCodec<PBECRuinedPortal> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("range_horizontal").forGetter(PBECRuinedPortal::rangeH),
                            IValue.CODEC.fieldOf("range_vertical").forGetter(PBECRuinedPortal::rangeY),
                            IValue.CODEC.fieldOf("range_starting_y").forGetter(PBECRuinedPortal::rangeStartY),
                            PBNBTHelper.arrayCodec(PBNBTHelper.arrayCodec(WeightedBlock.BLOCK_CODEC, () -> new WeightedBlock[0]), () -> new WeightedBlock[0][]).fieldOf("bricks").forGetter(PBECRuinedPortal::brickSet),
                            RandomizedItemStack.CODEC.listOf().fieldOf("loot").forGetter(PBECRuinedPortal::loot))
                    .apply(instance, PBECRuinedPortal::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int rangeH = this.rangeH.getValue(random);
        int rangeY = this.rangeY.getValue(random);
        int rangeStartY = this.rangeStartY.getValue(random);
        rangeY += rangeStartY;
        int time = rangeH * rangeH * rangeY;

        WeightedBlock[] bricks = WeightedSelector.selectWeightless(random, Arrays.asList(this.brickSet), this.brickSet.length);
        Direction.Axis axis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;

        return new PBEffectGenRuinedPortal(time, rangeH, rangeY, rangeStartY, PandorasBoxHelper.getRandomUnifiedSeed(random), bricks, loot, axis);
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