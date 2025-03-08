/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

import java.util.function.Function;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectRegistry {
    public static Tag writeEffect(PBEffect effect, RegistryAccess registryAccess) {
        if (effect != null) return PBEffect.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), effect).getOrThrow();
        return new CompoundTag();
    }

    public static PBEffect loadEffect(Tag tag, RegistryAccess registryAccess) {
        return PBEffect.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), tag).mapOrElse(Function.identity(), pbEffectError -> {
            PandorasBox.logger.error("Failed to parse box, using fallback. Error: " + pbEffectError);
            return new PBEffectDuplicateBox(PBEffectDuplicateBox.MODE_BOX_IN_BOX);
        });
    }
}
