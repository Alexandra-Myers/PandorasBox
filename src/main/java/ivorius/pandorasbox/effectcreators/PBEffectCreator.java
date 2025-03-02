/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.*;
import ivorius.pandorasbox.init.Init;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public interface PBEffectCreator {
    Codec<PBEffectCreator> CODEC = Init.BOX_EFFECT_CREATOR_REGISTRY.byNameCodec()
            .dispatch(PBEffectCreator::codec, mapCodec -> mapCodec);
    default PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        return new PBEffectDuplicateBox(0);
    }

    float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random);

    @NotNull MapCodec<? extends PBEffectCreator> codec();
}
