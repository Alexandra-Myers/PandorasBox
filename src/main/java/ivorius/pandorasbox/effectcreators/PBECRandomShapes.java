/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenShapes;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueHelper;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECRandomShapes(DValue range, DValue size, IValue number, EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks, ZValue sameBlockSetup) implements PBEffectCreator {
    public static final MapCodec<PBECRandomShapes> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECRandomShapes::range),
                            DValue.CODEC.fieldOf("size").forGetter(PBECRandomShapes::size),
                            IValue.CODEC.fieldOf("number").forGetter(PBECRandomShapes::number),
                            WeightedBlock.CODEC.fieldOf("blocks").forGetter(PBECRandomShapes::blocks),
                            ZValue.CODEC.fieldOf("same_block_setup").forGetter(PBECRandomShapes::sameBlockSetup))
                    .apply(instance, PBECRandomShapes::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        int number = this.number.getValue(random);
        int shape = random.nextInt(4) - 1;

        double[] size = ValueHelper.getValueRange(this.size, random);

        int time = Mth.floor((random.nextDouble() * 4.0 + 1.0) * size[1] * 8.0);
        boolean sameBlockSetup = this.sameBlockSetup.getValue(random);

        PBEffectGenShapes genTransform = new PBEffectGenShapes(time);
        Collection<WeightedBlock> modifiedBlocks = PandorasBoxHelper.assembleBlocks(blocks);
        if (sameBlockSetup) {
            genTransform.setShapes(random, PandorasBoxHelper.getRandomBlockList(random, modifiedBlocks), range, size[0], size[1], number, shape, PandorasBoxHelper.getRandomUnifiedSeed(random));
        } else {
            genTransform.setRandomShapes(random, modifiedBlocks, range, size[0], size[1], number, shape);
        }
        return genTransform;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.2f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
