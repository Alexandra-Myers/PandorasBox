/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenTrees;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZConstant;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECGenTrees(DValue range, DValue chancePerBlock, ZValue requiresSolidGround, IValue possibleTreeFlags) implements PBEffectCreator {
    public static final MapCodec<PBECGenTrees> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECGenTrees::range),
                        DValue.CODEC.fieldOf("chance_per_block").forGetter(PBECGenTrees::chancePerBlock),
                        ZValue.CODEC.optionalFieldOf("requires_solid_ground", new ZConstant(true)).forGetter(PBECGenTrees::requiresSolidGround),
                        IValue.CODEC.fieldOf("possible_tree_flags").forGetter(PBECGenTrees::possibleTreeFlags))
                .apply(instance, PBECGenTrees::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        double chancePerBlock = this.chancePerBlock.getValue(random);
        boolean requiresSolidGround = this.requiresSolidGround.getValue(random);
        int possibleTreeFlags = this.possibleTreeFlags.getValue(random);

        return new PBEffectGenTrees(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), requiresSolidGround, chancePerBlock, possibleTreeFlags);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
