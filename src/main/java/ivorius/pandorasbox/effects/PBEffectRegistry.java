/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.init.Init;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectRegistry {
    public static Class<? extends PBEffect> getEffect(String id) {
        return Init.BOX_EFFECT_REGISTRY.get(ResourceLocation.tryParse(id));
    }

    public static void writeEffect(PBEffect effect, CompoundTag compound, RegistryAccess registryAccess) {
        if (effect != null) {
            compound.putString("pbEffectID", effect.getEffectID());
            CompoundTag pbEffectCompound = new CompoundTag();
            effect.writeToNBT(pbEffectCompound, registryAccess);
            compound.put("pbEffectCompound", pbEffectCompound);
        }
    }

    public static PBEffect loadEffect(CompoundTag compound, RegistryAccess registryAccess) {
        return loadEffect(compound.getString("pbEffectID"), compound.getCompound("pbEffectCompound"), registryAccess);
    }

    public static PBEffect loadEffect(String id, CompoundTag compound, RegistryAccess registryAccess) {
        Class<? extends PBEffect> clazz = getEffect(id);

        PBEffect effect = null;

        if (clazz != null) {
            try {
                effect = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (effect != null && compound != null) {
            effect.readFromNBT(compound, registryAccess);
            return effect;
        } else {
            System.err.println("Pandoras Box: Could not load effect with id '" + id + "'!");
        }

        return null;
    }

    public static String getEffectID(PBEffect effect) {
        return Objects.requireNonNull(Init.BOX_EFFECT_REGISTRY.getKey(effect.getClass())).toString();
    }
}
