/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenReplace;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECReplace implements PBEffectCreator {
    public DValue range;

    public Block[] srcBlocks;
    public Collection<WeightedBlock> destBlocks;

    public ZValue takeRandomNearbyBlocks;

    public PBECReplace(DValue range, Block[] srcBlocks, Collection<WeightedBlock> destBlocks, ZValue takeRandomNearbyBlocks) {
        this.range = range;
        this.srcBlocks = srcBlocks;
        this.destBlocks = destBlocks;
        this.takeRandomNearbyBlocks = takeRandomNearbyBlocks;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        int baseX = Mth.floor(x);
        int baseY = Mth.floor(y);
        int baseZ = Mth.floor(z);
        boolean takeRandomNearbyBlocks = this.takeRandomNearbyBlocks.getValue(random);

        Block[] srcSelection = new Block[0];

        if (takeRandomNearbyBlocks) {
            List<WeightedBlock> nearbyBlocks = new ArrayList<>();
            for (int xP = -5; xP <= 5; xP++) {
                for (int yP = -5; yP <= 5; yP++) {
                    for (int zP = -5; zP <= 5; zP++) {
                        BlockState block = world.getBlockState(new BlockPos(baseX + xP, baseY + yP, baseZ + zP));

                        if (!block.isAir())
                            nearbyBlocks.add(new WeightedBlock(100, block.getBlock()));
                    }
                }
            }

            if (!nearbyBlocks.isEmpty()) {
                srcSelection = PandorasBoxHelper.getRandomBlockList(random, nearbyBlocks);
            }
        } else {
            srcSelection = srcBlocks.clone();
        }

        Block[] destSelection = PandorasBoxHelper.getRandomBlockList(random, destBlocks);

        return new PBEffectGenReplace(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), destSelection, srcSelection);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }
}
