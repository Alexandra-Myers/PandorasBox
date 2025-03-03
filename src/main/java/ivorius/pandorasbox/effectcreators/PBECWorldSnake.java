/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenWorldSnake;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECWorldSnake(IValue time, DValue startRange, DValue speed, DValue size,
                             EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks) implements PBEffectCreator {
    public static final MapCodec<PBECWorldSnake> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECWorldSnake::time),
                        DValue.CODEC.fieldOf("start_range").forGetter(PBECWorldSnake::startRange),
                        DValue.CODEC.fieldOf("speed").forGetter(PBECWorldSnake::speed),
                        DValue.CODEC.fieldOf("size").forGetter(PBECWorldSnake::size),
                        WeightedBlock.CODEC.fieldOf("blocks").forGetter(PBECWorldSnake::blocks))
                    .apply(instance, PBECWorldSnake::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double speed = this.speed.getValue(random);
        double size = this.size.getValue(random);
        int time = this.time.getValue(random);

        double distX = this.startRange.getValue(random);
        double distY = this.startRange.getValue(random);
        double distZ = this.startRange.getValue(random);

        Block[] selection = PandorasBoxHelper.getRandomBlockList(random, PandorasBoxHelper.assembleBlocks(blocks));

        return new PBEffectGenWorldSnake(time, selection, PandorasBoxHelper.getRandomUnifiedSeed(random), x + distX, y + distY, z + distZ, size, speed, random.nextFloat() * 360.0f, random.nextFloat() * 360.0f);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.2f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
