/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntityBased;
import ivorius.pandorasbox.effects.entity.TeleportEntityEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECTeleportEntities(float chanceForMoreEffects, IValue time, DValue range, DValue teleportRange, IValue teleports) implements PBEffectCreator {
    public static final MapCodec<PBECTeleportEntities> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.FLOAT.fieldOf("chance_for_more_effects").forGetter(PBECTeleportEntities::chanceForMoreEffects),
                            IValue.CODEC.fieldOf("time").forGetter(PBECTeleportEntities::time),
                            DValue.CODEC.fieldOf("range").forGetter(PBECTeleportEntities::range),
                            DValue.CODEC.fieldOf("teleport_range").forGetter(PBECTeleportEntities::teleportRange),
                            IValue.CODEC.fieldOf("teleports").forGetter(PBECTeleportEntities::teleports))
                    .apply(instance, PBECTeleportEntities::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        double range = this.range.getValue(random);
        double teleportRange = this.teleportRange.getValue(random);
        int teleports = this.teleports.getValue(random);

        return new PBEffectEntityBased(time, range, new TeleportEntityEffect(teleportRange, teleports));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return chanceForMoreEffects;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
